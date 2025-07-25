<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="荷兰文: LOCODE; 中文: UN/LOCODE" prop="locode">
        <el-input
          v-model="queryParams.locode"
          placeholder="请输入荷兰文: LOCODE; 中文: UN/LOCODE"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="荷兰文: Name; 中文: 地点名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入荷兰文: Name; 中文: 地点名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="荷兰文: NameWoDiacritics; 中文: 无重音符号地名" prop="nameWoDiacritics">
        <el-input
          v-model="queryParams.nameWoDiacritics"
          placeholder="请输入荷兰文: NameWoDiacritics; 中文: 无重音符号地名"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="Plus"
          @click="handleAdd"
          v-hasPermi="['infrastructure:unlocode:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['infrastructure:unlocode:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['infrastructure:unlocode:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['infrastructure:unlocode:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="unlocodeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="荷兰文: LOCODE; 中文: UN/LOCODE" align="center" prop="locode" />
      <el-table-column label="荷兰文: Name; 中文: 地点名称" align="center" prop="name" />
      <el-table-column label="荷兰文: NameWoDiacritics; 中文: 无重音符号地名" align="center" prop="nameWoDiacritics" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['infrastructure:unlocode:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['infrastructure:unlocode:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改UNLOCODE对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="unlocodeRef" :model="form" :rules="rules" label-width="80px">
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Unlocode">
import { listUnlocode, getUnlocode, delUnlocode, addUnlocode, updateUnlocode } from "@/api/infrastructure/unlocode"

const { proxy } = getCurrentInstance()

const unlocodeList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    locode: null,
    name: null,
    nameWoDiacritics: null
  },
  rules: {
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询UNLOCODE列表 */
function getList() {
  loading.value = true
  listUnlocode(queryParams.value).then(response => {
    unlocodeList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// 取消按钮
function cancel() {
  open.value = false
  reset()
}

// 表单重置
function reset() {
  form.value = {
    locode: null,
    name: null,
    nameWoDiacritics: null
  }
  proxy.resetForm("unlocodeRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  handleQuery()
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.locode)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加UNLOCODE"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _locode = row.locode || ids.value
  getUnlocode(_locode).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改UNLOCODE"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["unlocodeRef"].validate(valid => {
    if (valid) {
      if (form.value.locode != null) {
        updateUnlocode(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addUnlocode(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功")
          open.value = false
          getList()
        })
      }
    }
  })
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _locodes = row.locode || ids.value
  proxy.$modal.confirm('是否确认删除UNLOCODE编号为"' + _locodes + '"的数据项？').then(function() {
    return delUnlocode(_locodes)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('infrastructure/unlocode/export', {
    ...queryParams.value
  }, `unlocode_${new Date().getTime()}.xlsx`)
}

getList()
</script>
