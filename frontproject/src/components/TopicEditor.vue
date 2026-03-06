<script setup>
import {reactive,ref,computed} from "vue";
import {ElMessage, ElOption} from "element-plus";
import {Check, Document} from "@element-plus/icons-vue"
import MyEditor from "@/components/MyEditor.vue";
import {get, post} from "@/net/index.js";
import ColorDot from "@/components/ColorDot.vue";
import {useStore} from "@/store/index.js";
import {Delta} from "quill";
const store = useStore();
const props = defineProps({
  show:Boolean,
  defaultTitle: {
    default: '',
    type: String
  },
  defaultText: {
    default: '',
    type: String
  },
  defaultType: {
    default: null,
    type: Number
  },
  submitButton: {
    default: '立即发表主题',
    type: String
  },
  submit:{
    default: (editor, success) => {
      post("api/forum/create-topic", {
        type: editor.type.id,
        title: editor.title,
        content: editor.text,
      }, ()=>{
        ElMessage.success("帖子发表成功!");
        success();
      })
    },
    type: Function
  }
});



const emit = defineEmits(["close", "success"]);
const refEditor = ref()
const editor = reactive({
  type: null,
  title: '',
  text: {},
  loading: false,
})

function initEditor() {
  if(props.defaultText){
    editor.text = new Delta(JSON.parse(props.defaultText));
  } else{
    try {
      const delta = new Delta();
      refEditor.value.quill.setContents(delta);
    } catch (error) {
      // console.error("Quill渲染失败:", error);
    }
  }
  editor.title = props.defaultTitle
  editor.type = findTypeById(props.defaultType)
}

function findTypeById(id){
  for (let type of store.forum.types) {
    if(type.id === id)
      return type
  }
}

function deltaToText(delta) {
  if(!delta.ops) return "";
  let str = "";
  for(let op of delta.ops) str += op.insert;
  return str.replace(/\s/g, "");
}

const contentLength = computed(() => deltaToText(editor.text).length );

function submitTopic() {
  const text = deltaToText(editor.text);
  if(text.length > 20000){
    ElMessage.warning("字数超出范围，无法发表主题")
    return
  }
  if(!editor.title){
    ElMessage.warning ("请填写标题")
    return
  }
  if(!editor.type) {
    ElMessage.warning("请选择一个合适的帖子标题")
    return
  }
  props.submit(editor, () => emit('success'));

}
</script>

<template>
  <el-drawer :model-value="show"
             :direction="'btt'"
             :size="650"
             @open="initEditor"
             :close-on-click-modal="false"
             @close="emit('close')"
             style="width: 800px; margin: auto; border-radius: 10px 10px 0 0;"
   >
    <template #header>
      <div>
        <div style="font-weight: bold">发表新的帖子</div>
        <div style="font-size: 13px">孩子们，不要在我的网站发表不文明言论</div>
      </div>
    </template>

    <div style="display: flex; gap: 10px">
      <div style="width: 160px">
        <el-select placeholder="请选择帖子类型..." value-key="id" v-model="editor.type" :disabled="!store.forum.types.length">
          <el-option v-for="item in store.forum.types.filter(type => type.id > 0)" :value="item" :label="item.name">
            <div>
              <ColorDot :color="item.color"></ColorDot>
              <span style="margin-left: 10px">{{item.name}}</span>
            </div>
          </el-option>
        </el-select>
      </div>
      <div style="flex: 1;">
        <el-input placeholder="请输入帖子标题" :prefix-icon="Document" v-model="editor.title" maxlength="60"></el-input>
      </div>
    </div>
    <div style="margin-top: 10px; font-size: 13px; color: gray">
      <ColorDot :color="editor.type? editor.type.color : '#FFFFFF'"></ColorDot>
      {{editor.type? editor.type.description : '请在上面选择一个帖子标题'}}
    </div>

    <div style="height: 400px">
        <MyEditor style="margin-top: 10px; height: calc(100% - 60px); border-radius: 5px"
                  v-model="editor.text"
                  placeholder="今天想分享点什么呢 ..."
                  content-type="delta"
                  v-loading="editor.loading"
                  element-loading-text="正在上传图片，请稍后..."
                  ref="refEditor"
                  v-if="show"
        />


    </div>
    <div style="display: flex; justify-content: space-between; margin-top: 15px">
      <div style="color: gray; font-size: 13px">
        当前字数：{{contentLength}} (最大支持 30000 字)
      </div>
      <div>
        <el-button type="success" :icon="Check" @click="submitTopic()" plain>{{submitButton}}</el-button>
      </div>
    </div>
  </el-drawer>
</template>

<style scoped>
:deep(.el-drawer){
  width: 800px;
  margin:auto;
  border-radius: 10px 10px 0 0;
}

:deep(.ql-toolbar){
  border-radius: 10px 10px 0 0;
  border-color: var(--el-border-color);
}

:deep(.ql-container){
  border-radius: 0 0 10px 10px;
  border-color: var(--el-border-color);
}
:deep(.ql-snow .ql-tooltip){
  transform: translate(85px, 10px);
}
:deep(.el-drawer__header){
  margin: 0;
}
</style>