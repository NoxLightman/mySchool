<template>
  <div v-loading="loading" class="main-content" element-loading-text="正在进入,请稍后...">
    <el-container v-if="!loading" style="height: 100%">
      <el-header class="main-content-header">
        <el-image class="logo" src="https://element-plus.org/images/element-plus-logo.svg"></el-image>
        <div style="flex: 1; padding:0 20px; text-align: center">
          <el-input placeholder="搜索内容相关内容..." style="width:100%; max-width: 500px">
            <template #prefix>
              <el-icon><Search/></el-icon>
            </template>

            <template #append>
              <el-select  v-model="searchInput.type" style="width: 120px">
                <el-option label="帖子广场" value="1"></el-option>
                <el-option label="表白墙" value="2"></el-option>
                <el-option label="校园活动" value="3"></el-option>
                <el-option label="失物招领" value="4"></el-option>
                <el-option label="教务通知" value="5"></el-option>
              </el-select>

            </template>
          </el-input>
        </div>
        <div class="user-info">
          <el-popover placement="bottom" :width="350" trigger="click">
            <template #reference>
              <el-badge is-dot style="margin-right: 20px" :hidden="!notification.length">
                <div class="notification">
                  <el-icon><Bell/></el-icon>
                  <div style="font-size: 10px">消息</div>
                </div>
              </el-badge>
            </template>
            <el-empty :image-size="80" description="无事发生~~" v-if="!notification.length"/>
            <el-scrollbar :max-height="500" v-else>
              <light-card v-for="item in notification" class="notification"
                          @click="confirmNotification(item.id, item.url)">
                <div>
                  <el-tag size="small" :type="item.type">消息</el-tag>&nbsp;
                  <span style="font-weight: bold; font-size: 14px">{{item.title}}</span>
                </div>
                <el-divider style="margin: 7px 0 3px 0"/>
                <div style="font-size: 13px; color: grey">
                  {{item.content}}
                </div>
              </light-card>
              <div style="margin-top: 20px">
                <el-button size="small" type="info" :icon="Check" @click="deleteAllNotification"
                           style="width:100%" plain>清除未读消息</el-button>
              </div>
            </el-scrollbar>
          </el-popover>

          <div class="profile">

            <div>{{store.user.username}}</div>
            <div>{{store.user.email}}</div>
          </div>
          <el-dropdown>
            <el-avatar :src="store.avatarUrl"/>
            <template #dropdown>
              <el-dropdown-item>
                <el-icon><Operation/></el-icon>
                个人设置
              </el-dropdown-item>
              <el-dropdown-item>
                <el-icon><Message/></el-icon>
                个人信息
              </el-dropdown-item>
              <el-dropdown-item divided @click="userLogout()">
                <el-icon><Back/></el-icon>
                退出登录
              </el-dropdown-item>

            </template>>
          </el-dropdown>
        </div>
      </el-header>

      <el-container>
        <el-aside width="230px">
          <el-scrollbar style="height: calc(100% - 55px)">
            <el-menu
                :default-active="$route.path"
                :default-openeds="['1','2','3']"
                router
                style="height: calc(100% - 55px)">
              <el-sub-menu index="1">
                <template #title>
                  <el-icon><Location/></el-icon>
                  <span><b>校园论坛</b></span>
                </template>
                <el-menu-item index="/index">
                  <template #title>
                    <el-icon><ChatDotSquare/></el-icon> 帖子广场
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><switch-filled/></el-icon> 表白墙
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Notification/></el-icon> 校园活动
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Shop/></el-icon> 失物招领
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><School/></el-icon> 海文考研
                    <el-tag size="small" style="margin-left: 10px">合作机构</el-tag>
                  </template>
                </el-menu-item>

              </el-sub-menu>
              <el-sub-menu index="2">
                <template #title>
                  <el-icon><Soccer/></el-icon>
                  <span><b>探索与发现</b></span>
                </template>
                <el-menu-item>
                  <template #title>
                    <el-icon><switch-filled/></el-icon> 班级课程表
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Fries/></el-icon> 成绩查询
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Orange/></el-icon> 教务系统
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Notebook/></el-icon> 在线图书馆
                  </template>
                </el-menu-item>
                <el-menu-item>
                  <template #title>
                    <el-icon><Flag/></el-icon> 预约教室
                  </template>
                </el-menu-item>
              </el-sub-menu>
              <el-sub-menu index="3">
                <template #title>
                  <el-icon><User/></el-icon>
                  <span><b>个人设置</b></span>
                </template>
                <el-menu-item index="/index/user-setting">
                  <template #title>
                    <el-icon><Medal/></el-icon> 个人信息设置
                  </template>
                </el-menu-item>
                <el-menu-item index="/index/privacy-setting">
                  <template #title>
                    <el-icon><Aim/></el-icon> 账号安全设置
                  </template>
                </el-menu-item>
              </el-sub-menu>
            </el-menu>
          </el-scrollbar>
        </el-aside>
        <el-main class="main-content-page" style="height: 100%; padding: 0;">
          <el-scrollbar style="height: calc(100% - 55px)">
            <router-view v-slot="{ Component }">
              <transition mode="out-in" name="el-fade-in-linear">
                <component :is="Component" style="height: 100%"/>
              </transition>
            </router-view>
          </el-scrollbar>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>


<script setup>
import {ref,reactive} from "vue";
import {ElHeader, ElAside, ElMain, ElSubMenu, ElMenuItem, ElOption, ElDropdownItem, ElMessage} from 'element-plus'
import {get} from '@/net'
import {useStore} from "@/store";
import {
  Aim, Back, Bell,
  ChatDotSquare, Check,
  Flag, Fries,
  Location, Medal, Message, MostlyCloudy,
  Notebook,
  Notification, Operation, Orange,
  School, Search,
  Shop, Soccer,
  SwitchFilled, User
} from "@element-plus/icons-vue";
import router from "@/router";
import LightCard from "@/components/LightCard.vue";

const loading = ref(true);
const store = useStore();
const searchInput = reactive({
  type: '1',
  text: ''
});

get('api/user/info', (data)=>{
  store.user = data;
  loading.value = false;
})

function userLogout() {
  router.push('/');
  ElMessage.success("成功退出，欢迎再次使用");
}

const notification = ref([]);
const loadNotification = () => get(`/api/notification/list`, data => notification.value = data)
loadNotification();

function confirmNotification(id, url) {
  get(`/api/notification/delete?id=${id}`, ()=>{
    loadNotification();
    window.open(url);
  })
}

function deleteAllNotification() {
  get(`api/notification/delete-all`, loadNotification);
}
</script>

<style lang="less" scoped>
.notification-item {
  transition: .3s;
  &:hover {
    cursor: pointer;
    opacity: 0.7;
  }
}

.notification{
  font-size: 22px;
  line-height: 14px;
  text-align: center;
  transition: color .3s;

  &:hover{
    color: gray;
    cursor: pointer;
  }
}

.main-content {
  height: 100vh;
  width: 100vw;
}

.main-content-header {
  border-bottom: solid 1px var(--el-border-color);
  height: 55px;
  display: flex;
  align-items: center;
  box-sizing: border-box;
}

.main-content-page {
  padding: 0;
  background-color: #f7f8fa;
}

.dark .main-content-page {
  padding: 0;
  background-color: #242628;
}


.logo{
  height: 32px;
}

.user-info{
  display: flex;
  justify-content: flex-end;
  align-items: center;

  .profile {
    text-align: right;
    margin-right: 20px;

    :first-child {
      font-size: 16px;
      font-weight: bold;
      line-height: 20px;
    }

    :last-child {
      font-size: 10px;
      color: gray;
    }
  }
}

.el-avatar:hover {
  cursor: pointer;
}

</style>