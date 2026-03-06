<script setup>
import {computed, reactive, ref, watch} from "vue";
import LightCard from "@/components/LightCard.vue";
import {
  ArrowRight, ArrowRightBold,
  Calendar, Cherry,
  Clock,
  Collection,
  Compass,
  Document,
  Edit,
  EditPen, FolderOpened,
  Link,
  Microphone,
  Picture, Star
} from "@element-plus/icons-vue";
import Weather from "@/components/Weather.vue";
import {get} from "@/net/index.js";
import {ElMessage} from "element-plus";
import TopicEditor from "@/components/TopicEditor.vue";
import {useStore} from "@/store/index.js";
import axios from "axios";
import ColorDot from "@/components/ColorDot.vue";
import router from "@/router/index.js";
import TopicTag from "@/components/TopicTag.vue";
import InteractButton from "@/components/InteractButton.vue";
import TopicCollectList from "@/components/TopicCollectList.vue";

const editor = ref(false);
const topics = reactive({
  list: [],
  type: 0,
  page: 0,
  end: false,
  top: []
});

const type = ref(0);

watch(type, () => {
  resetList();
}, {immediate: true});

const store = useStore();

const today = computed(() => {
  const date = new Date();
  return `${date.getFullYear()} 年 ${date.getMonth()+1} 月 ${date.getDate()} 日`;
});


get('api/forum/top-topic', data => {
  topics.top = data;
})


function updateList() {
  if(topics.end) return;
  get(`api/forum/list-topic?page=${topics.page}&type=${type.value}`, data => {
    if(data) {
      data.forEach(d => topics.list.push(d))
      topics.page++;
    }
    if(!data || data.length < 10){
      topics.end = true;
    }
  });
}

updateList();

function onTopicCreate(){
  editor.value = false;
  resetList();
}

function resetList(){
  topics.page = 0
  topics.list = []
  topics.end = false
  updateList();
}

const weather = reactive({
  location: {},
  now: {},
  hourly: [],
  success: false,
});

const collects = ref(false);

navigator.geolocation.getCurrentPosition(position => {
  const longitude = position.coords.longitude;
  const latitude = position.coords.latitude;
  get(`/api/forum/weather?longitude=${longitude}&latitude=${latitude}`, data => {
    Object.assign(weather, data);
    weather.success = true;
  })
}, error => {
  console.info(error)
  ElMessage.warning('位置信息获取超时，请检测网络设置')
  get(`api/forum/weather?longitude=116.40&latitude=39.90`, data => {
    Object.assign(weather, data);
    weather.success = true;
  })
}, {
  timeout: 5000,
  enableHighAccuracy: true,
})
</script>

<template>
  <div style="display: flex; margin: 20px auto; gap: 5px; max-width: 900px; padding:0 20px">
    <div style="flex: 1; margin-right: 30px">
      <LightCard>
        <div class="create-topic" @click="editor=true">
          <el-icon style="translate: 0 2px"><EditPen/></el-icon>
          点击发表主题 ......
        </div>
        <div style="margin-top: 10px; display: flex; gap: 13px; font-size: 18px; color: gray">
          <el-icon><Edit/></el-icon>
          <el-icon><Document/></el-icon>
          <el-icon><Compass/></el-icon>
          <el-icon><Picture/></el-icon>
          <el-icon><Microphone/></el-icon>
        </div>
      </LightCard>
      <lightCard style="margin-top: 10px; display:flex; flex-direction: column; gap: 10px">
        <div v-for="item in topics.top" class="top-topic" @click="router.push(`/index/topic-detail/${item.id}`)">
          <el-tag type="info" size="small">置顶</el-tag>
          <div>{{item.title}}</div>
          <div>{{new Date(item.time).toLocaleDateString()}}</div>
        </div>
      </lightCard>

      <light-card style="margin-top: 10px; display: grid; grid-template-columns: repeat(5, 1fr); grid-gap: 10px;">
        <div :class="`type-select-card ${type === item.id ? 'active': ''}`"
              v-for="item in store.forum.types" @click="type = item.id">
          <ColorDot :color="item.color" />
          {{item.name}}
        </div>
      </light-card>
      <transition name="el-fade-in" mode="out-in">
        <div v-if="topics.list.length">
          <div style="margin-top: 10px; display: flex; flex-direction: column; gap: 5px;" v-infinite-scroll="updateList">
              <lightCard v-for="item in topics.list" class="text-card" @click="router.push(`/index/topic-detail/${item.id}`)">
                <div style="display: flex">
                  <div>
                    <el-avatar :size="30" :src="`${store.avatarUserUrl(item.avatar)}`"/>
                  </div>
                  <div style="margin-left: 7px; margin-bottom: 7px;">
                    <div style="font-size: 13px; font-weight: bold;">{{item.username}}</div>
                    <div style="font-size: 12px; color: gray">
                      <el-icon style="translate: 0 1px"><Clock/></el-icon>
                      {{new Date(item.time).toLocaleString() }}
                    </div>
                  </div>
                </div>
                <div>
                  <TopicTag :type="item.type" />
                  <span style="font-weight: bold">{{item.title}}</span>
                </div>
                <div class="topic-content"> {{item.text}} </div>
                <div style="display: grid; grid-template-columns: repeat(3, 1fr); grid-gap: 20px;">
                  <el-image class="topic-image" v-for="img in item.images" :src="img" fit="cover"></el-image>
                </div>
                <div style="display: flex; gap: 20px; font-size: 13px; margin-top: 10px; opacity: 0.7">
                  <div>
                    <el-icon style="vertical-align: middle; translate: 0 -1px; margin-right: 1px"><Cherry/></el-icon>{{item.like}} 点赞
                  </div>
                  <div>
                    <el-icon style="vertical-align: middle; translate: 0 -1px; margin-right: 1px"><Star/></el-icon>{{item.collect}} 收藏
                  </div>
                </div>
              </lightCard>
          </div>
        </div>
      </transition>
    </div>

    <div style="width: 280px;">
      <div style="position:sticky; top: 20px">
        <lightCard>
          <div class="collect-list-button" @click="collects = true">
            <span><el-icon><FolderOpened/></el-icon>查看我的收藏</span>
            <el-icon style="translate: 0 3px"><ArrowRightBold/></el-icon>
          </div>
        </lightCard>
        <LightCard style="margin-top: 10px">
          <el-icon style="translate: 0 2px"><Collection/></el-icon>
          论坛公告
          <el-divider style="margin: 10px 0"/>
          <div style="font-size: 14px; margin: 10px; color: gray">
            MySQL 8.0 移除查询缓存的决策，是权衡性能、维护成本和实际效用后的结果。
            虽然查询缓存在特定场景（如静态数据重复查询）可能有效，但其全局锁、高维护成本及低命中率使其在高并发、动态数据环境下弊大于利。
            建议通过外部缓存工具或优化 InnoDB 缓冲池来替代。
          </div>
        </LightCard>
        <LightCard>
          <el-icon><calendar/></el-icon>
          天气信息
          <el-divider style="margin: 10px 0"/>
          <Weather :data="weather" />
        </LightCard>
        <div style="margin-top: 10px">
          <LightCard>
            <div class="info-text">
              <div>当前日期</div>
              <div>{{today}}</div>
            </div>
            <div class="info-text" style="margin-top: 10px">
              <div>当前ip地址</div>
              <div>127.0.0.1</div>
            </div>
          </LightCard>
        </div>
        <div style="font-size: 14px; margin-top: 10px; color: gray">
          <el-icon style="translate: 0 2px"><Link/></el-icon>
          友情链接
          <el-divider style="margin: 10px 0"/>
        </div>
        <div style="display: grid; grid-template-columns: repeat(2, 1fr); grid-gap: 10px; margin-top: 10px">
          <div class="friend-link">
            <a href="https://www.xiaomiev.com/ultra">
            <el-image style="height: 100%" src="https://tse2-mm.cn.bing.net/th/id/OIP-C.S7KOLex5iCFR-OmnjyG6GAHaD_?rs=1&pid=ImgDetMain"/>
            </a>
          </div>
          <div class="friend-link">
            <a href="https://www.xiaomiev.com/ultra">
            <el-image style="height: 100%" src="https://tse3-mm.cn.bing.net/th/id/OIP-C.xkX7k2KgAyUoam4ITGLUkQHaE2?w=246&h=180&c=7&r=0&o=5&pid=1.7"/>
            </a>
          </div>
          <div class="friend-link">
            <a href="https://www.xiaomiev.com/ultra">
            <el-image style="height: 100%" src="https://tse1-mm.cn.bing.net/th/id/OIP-C.YbiGuuE12YNGyQIzbqMb2QHaFj?w=215&h=180&c=7&r=0&o=5&pid=1.7"/>
            </a>
          </div>
          <div class="friend-link">
            <a href="https://www.xiaomiev.com/ultra">
              <el-image style="height: 100%" src="https://tse1-mm.cn.bing.net/th/id/OIP-C.Umup2WRG2IfDHncTCPc6JQHaE0?w=273&h=180&c=7&r=0&o=5&pid=1.7"/>
            </a>
          </div>
        </div>
      </div>
    </div>
    <TopicEditor :show="editor" @success="onTopicCreate();" @close="editor=false" ></TopicEditor >
    <topic-collect-list :show="collects" @close="collects = false"></topic-collect-list>
  </div>
</template>


<style class="less" scoped>
.collect-list-button{
  font-size: 14px;
  display: flex;
  justify-content: space-between;
  transition: .3s;

  &:hover{
    cursor: pointer;
    opacity: 0.6;
  }
}


.top-topic{
  display: flex;

  div:first-of-type{
    font-size: 14px;
    margin-left: 10px;
    font-weight: bold;
    opacity: 0.8;
    transition: color .3s;

    &:hover{
      color: gray;
    }
  }

  div:nth-of-type(2){
    flex: 1;
    color: gray;
    font-size: 12px;
    text-align: right;
  }

  &:hover{
    cursor: pointer;
  }
}

.type-select-card {
  background-color: #f5f5f5;
  padding: 2px 7px;
  font-size: 12px;
  border-radius: 4px;
  box-sizing: border-box;
  transition: background-color .3s;
  border: 1px solid #fff3f3;

  &.active {
    border: 1px solid #ead4c4;
  }

  &:hover{
    cursor: pointer;
    background-color: #dadada;
  }
}

.text-card {
  padding:15px;
  transition: scale .5s;

  &:hover {
    scale: 1.015;
    cursor: pointer;
  }

  .topic-content {
    font-size: 13px;
    color: gray;
    margin: 5px 0;
    display: -webkit-box;
    -webkit-box-orient: vertical;
    -webkit-line-clamp: 2;
    overflow: hidden;
    text-overflow: ellipsis;
  }



  .topic-image {
    width: 100%;
    height: 100%;
    max-height: 110px;
    border-radius: 5px;

  }
}

.info-text{
  display: flex;
  justify-content: space-between;
  color: grey;
  font-size: 14px;
}

.friend-link{
  border-radius: 5px;
  overflow: hidden;
}

.create-topic {
  background: #ededed;
  border-radius: 5px;
  height: 40px;
  font-size: 14px;
  line-height: 40px;
  padding: 0 20px;
  color: #737373;
  &:hover {
    cursor: pointer;
  }
}

.dark {
  .create-topic {
    background-color: #232323;
  }

  .type-select-card {
    background-color: #282828;

    &.active {
      border: solid 1px #64594b;
    }

    &:hover {
      background-color: #5e5e5e;
    }
  }
}
</style>