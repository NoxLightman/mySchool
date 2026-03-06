<script setup>
import {reactive,computed,ref} from "vue";
import {useRoute} from "vue-router";
import {get,post} from "@/net/index.js";
import axios from "axios";
import {ArrowLeft, ChatSquare, Cherry, Delete, EditPen, Female, Male, Plus, Star} from "@element-plus/icons-vue";
import {QuillDeltaToHtmlConverter} from "quill-delta-to-html";
import Card from "@/components/Card.vue";
import router from "@/router/index.js";
import TopicTag from "@/components/TopicTag.vue";
import InteractButton from "@/components/InteractButton.vue";
import {ElMessage} from "element-plus";
import {useStore} from "@/store/index.js";
import TopicEditor from "@/components/TopicEditor.vue";
import TopicCommentEditor from "@/components/TopicCommentEditor.vue";
const route = useRoute()
const tid = route.params.tid

const topic = reactive({
  data: null,
  comment: null,
  like: false,
  collect: false,
  page: 1,
});

const edit = ref(false);
const store = useStore();
const comment = reactive({
  show: false,
  text: '',
  quote: null,
})

const init = () => get(`api/forum/topic?tid=${tid}`, data => {
  topic.data = data;
  topic.like = data.interact.like;
  topic.collect = data.interact.collect;
  loadComments(1);
  // console.log(topic.data);
});
init();

function convertToHtml(content) {
  const ops = JSON.parse(content).ops;
  const converter = new QuillDeltaToHtmlConverter(ops, {inlineStyles: true})
  return converter.convert();
}

const comments = reactive({
    show: false,
    text: '',
    quote: -1,
})

function loadComments(page) {
  topic.comment = null;
  topic.page = page;
  get(`api/forum/comments?tid=${tid}&page=${page - 1}`, data => topic.comment = data);
}

function interact(type, message) {
  get(`/api/forum/interact?tid=${tid}&type=${type}&state=${!topic[type]}`, () => {
    topic[type] = !topic[type];
    if(topic[type]) {
      ElMessage.success(`${message}成功!`);
    } else {
      ElMessage.success(`${message}已取消`);
    }
  })
}

function updateTopic(editor, topic) {
  post('api/forum/update-topic', {
    id: tid,
    type: editor.type.id,
    title: editor.title,
    content: editor.text,
  }, ()=>{
    ElMessage.success('帖子内容更新成功');
    edit.value = false;
    init();
  })
}

function onCommentAdd(){
  comment.show = false;
  loadComments(Math.floor(++topic.data.comments / 10) + 1);
}

function deleteComment(id) {
  get(`api/forum/delete-comment?id=${id}`, () => {
    ElMessage.success('删除评论成功');
    loadComments(topic.page)
  })
}
</script>

<template>
  <div class="topic-page" v-if="topic.data" >
    <div class="topic-main" style="z-index: 10; position: sticky; top: 0;">
      <Card style="display: flex; width: 100%;">
        <el-button :icon="ArrowLeft" type="info" size="small" plain round
          @click="router.push('/index')">返回列表</el-button>
        <div style="text-align: center; flex: 1; font-weight: bold;">
          <TopicTag :type="topic.data.type"/>
          <span style="margin-left: 7px">{{topic.data.title}}</span>
        </div>
      </Card>
    </div>

    <div class="topic-main">
      <div class="topic-main-left">
        <el-avatar :src="store.avatarUserUrl(topic.data.user.avatar)" :size="60"/>
        <div>
          <div style="font-size: 18px; font-weight: bold;">
            {{topic.data.user.username}}
            <span style="color: hotpink" v-if="topic.data.user.gender === 1">
              <el-icon><Male/></el-icon>
            </span>
            <span style="color: dodgerblue" v-if="topic.data.user.gender === 0">
              <el-icon><Female/></el-icon>
            </span>
          </div>
          <div class="description" style="font-size: 13px; color: gray">{{topic.data.user.email}}</div>
        </div>
        <el-divider style="margin: 10px 0"/>
        <div style="text-align: left; margin: 0 5px; font-size: 12px; color: gray;" >
            <div class="description">手机号：{{topic.data.user.phone || '已隐藏或未填写'}}</div>
            <div class="description">QQ：{{topic.data.user.qq || '已隐藏或未填写'}}</div>
            <div class="description">wx：{{topic.data.user.wx || '已隐藏或未填写'}}</div>
        </div>
        <el-divider style="margin: 10px 0"/>
        <div class="description" style="margin: 0  5px">{{topic.data.user.description}}</div>
      </div>
      <div class="topic-main-right">
        <div class="topic-content" v-html="convertToHtml(topic.data.content)"></div>
        <el-divider/>
        <div style="font-size: 13px; color: gray; text-align: center;">
            <div>发帖时间: {{new Date(topic.data.time).toLocaleString()}}</div>
        </div>

        <div style="text-align: right; margin-top: 25px">
          <interact-button name="编辑帖子" check-name="编辑帖子" color="dodgerblue" :check="true" @check="edit=true" v-if="store.user.id === topic.data.user.id">
            <el-icon style="translate: 0 2px; margin-right: 3px"><EditPen/></el-icon>
          </interact-button>
          <interact-button name="点个赞吧" check-name="已点赞" color="pink" :check="topic.like" @check="interact('like', '点赞')">
             <el-icon style="translate: 0 2px; margin-right: 3px"><Cherry/></el-icon>
          </interact-button>
          <interact-button name="点收藏不迷路" check-name="已收藏" color="orange" :check="topic.collect" @check="interact('collect', '收藏')">
             <el-icon style="translate: 0 1px; margin-right: 3px"><Star/></el-icon>
          </interact-button>
        </div>
      </div>
    </div>
    <transition name = "el-fade-in-linear" mode="out-in">
      <div v-if="topic.comment">
        <div class="topic-main" style="margin-top:10px" v-for="item in topic.comment">
          <div class="topic-main-left">
            <el-avatar :src="store.avatarUserUrl(item.user.avatar)" :size="60"/>
            <div>
              <div style="font-size: 18px; font-weight: bold;">
                {{item.user.username}}
                <span style="color: hotpink" v-if="item.user.gender === 1">
                 <el-icon><Male/></el-icon>
                </span>
                <span style="color: dodgerblue" v-if="item.user.gender === 0">
                  <el-icon><Female/></el-icon>
                </span>
              </div>
            </div>
          </div>
          <div class="topic-main-right">
            <div v-if="item.quote" class="comment-quote">
              <div>回复：{{item.quote}}</div>
            </div>

            <div style="font-size: 13px; color: gray; text-align: left;">
              <div>评论时间: {{new Date(item.time).toLocaleString()}}</div>
            </div>
            <div class="topic-content" v-html="convertToHtml(item.content)"></div>
            <div style="text-align: right">
              <el-link :icon="ChatSquare" @Click="comment.show = true; comment.quote=item"
                       type="info">&nbsp; 回复评论 </el-link>
              <el-link :icon="Delete" type="danger" v-if="item.user.id === store.user.id" @Click="deleteComment(item.id)"
                       style="margin-left: 20px">&nbsp; 删除评论</el-link>
            </div>

            <div style="text-align: right; margin-top: 25px">
            </div>
          </div>
        </div>
        <div style="width: fit-content; margin: 20px auto">
          <el-pagination background layout="prev, pager, next" v-model:current-page="topic.page" @current-change="loadComments"
                         :total="topic.data.comments" :page-size="10"/>
        </div>
      </div>

    </transition>

    <topic-editor :show="edit" @close="edit=false" v-if="topic.data && store.forum.types"
      :default-text="topic.data.content"
      :default-title="topic.data.title"
      :default-type="topic.data.type"
      submit-button="更新帖子内容"
      :submit="updateTopic"/>

    <topic-comment-editor :show="comment.show" @close="comment.show = false"
                          :quote="comment.quote" :tid="tid" @comment="onCommentAdd"/>
    <div class="add-comment" @click="comment.show = true; comment.quote = null" >
      <el-icon><Plus/></el-icon>
    </div>
  </div>

</template>

<style class="less" scoped>
.comment-quote{
  font-size: 13px;
  color: gray;
  background-color: rgba(94,94,94,0.2);
  padding: 10px;
  margin-top: 10px;
  border-radius: 5px;
}

.add-comment {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  font-size: 18px;
  line-height: 45px;
  text-align: center;
  color: var(--el-color-primary);
  background: var(--el-bg-color-overlay);
  box-shadow: var(--el-box-shadow-lighter);

  &:hover {
    background: var(--el-border-color-extra-light);
    cursor: pointer;
  }
}

.topic-page {
  display: flex;
  flex-direction: column;
  gap: 10px;
  padding: 10px 0;

}

.topic-main{
  display: flex;
  border-radius: 7px;
  margin: 0 auto;
  background-color: var(--el-bg-color);
  width: 800px;

  .topic-main-left{
    width: 200px;
    padding: 10px;
    text-align: center;
    border-right: solid 1px var(--el-border-color);
  }

  .topic-main-right{
    width: 600px;
    padding: 10px 20px;
    display: flex;
    flex-direction: column;

    .topic-content{
      font-size: 14px;
      line-height: 22px;
      opacity: 0.8;
      flex: 1;
    }

    .description{
      font-size: 12px;
      color: gray;
    }
  }
}
</style>
