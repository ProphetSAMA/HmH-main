<!--<script setup>-->

<!--</script>-->

<!--<template>-->

<!--</template>-->

<!--<style scoped>-->

<!--</style>-->
<template>
  <el-container>
    <!-- 顶栏 -->
    <el-header class="header">
      <h2>公告列表</h2>
    </el-header>

    <!-- 公告列表 -->
    <el-main>
      <el-table
          :data="announcementList"
          style="width: 100%"
          stripe
          border
      >
        <el-table-column label="公告标题" prop="title" width="300"></el-table-column>
        <el-table-column label="发布时间" prop="date" width="200"></el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="{ row }">
            <el-button size="small" @click="viewAnnouncement(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-main>
  </el-container>
</template>

<script setup>
import { ref } from 'vue'
import { ElButton } from 'element-plus'

const announcementList = ref([
  {
    title: '系统维护公告',
    date: '2025-03-24',
    content: '我们将在 2025年3月25日进行系统维护，预计停机1小时。'
  },
  {
    title: '年度报告发布',
    date: '2025-03-20',
    content: '2025年年度报告已发布，请大家查看。'
  },
  {
    title: '春节放假通知',
    date: '2025-01-15',
    content: '春节期间放假安排，具体日期为2025年2月1日至2025年2月10日。'
  }
])

// 查看公告的处理函数
const viewAnnouncement = (announcement) => {
  // 在这里你可以实现查看详细内容的功能
  alert(`公告标题: ${announcement.title}\n公告内容: ${announcement.content}`)
}
</script>

<style scoped>
.header {
  background-color: #f1f1f1;
  padding: 10px;
  font-size: 20px;
  text-align: center;
}

.el-table {
  margin-top: 20px;
}
</style>
