<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="${comment}" prop="descriptionEn">
        <el-input
          v-model="queryParams.descriptionEn"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="descriptionCn">
        <el-input
          v-model="queryParams.descriptionCn"
          placeholder="请输入${comment}"
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
          v-hasPermi="['weather:codeDict:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['weather:codeDict:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['weather:codeDict:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['weather:codeDict:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="codeDictList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="${comment}" align="center" prop="code" />
      <el-table-column label="${comment}" align="center" prop="descriptionEn" />
      <el-table-column label="${comment}" align="center" prop="descriptionCn" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['weather:codeDict:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['weather:codeDict:remove']">删除</el-button>
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

    <!-- 添加或修改weatherCodeDict对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="codeDictRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="${comment}" prop="descriptionEn">
          <el-input v-model="form.descriptionEn" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="descriptionCn">
          <el-input v-model="form.descriptionCn" placeholder="请输入${comment}" />
        </el-form-item>
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

<script setup name="CodeDict">
import { listCodeDict, getCodeDict, delCodeDict, addCodeDict, updateCodeDict } from "@/api/weather/codeDict"

const { proxy } = getCurrentInstance()

const codeDictList = ref([])
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
    descriptionEn: null,
    descriptionCn: null
  },
  rules: {
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询weatherCodeDict列表 */
function getList() {
  loading.value = true
  listCodeDict(queryParams.value).then(response => {
    codeDictList.value = response.rows
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
    code: null,
    descriptionEn: null,
    descriptionCn: null
  }
  proxy.resetForm("codeDictRef")
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
  ids.value = selection.map(item => item.code)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加weatherCodeDict"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _code = row.code || ids.value
  getCodeDict(_code).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改weatherCodeDict"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["codeDictRef"].validate(valid => {
    if (valid) {
      if (form.value.code != null) {
        updateCodeDict(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addCodeDict(form.value).then(response => {
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
  const _codes = row.code || ids.value
  proxy.$modal.confirm('是否确认删除weatherCodeDict编号为"' + _codes + '"的数据项？').then(function() {
    return delCodeDict(_codes)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('weather/codeDict/export', {
    ...queryParams.value
  }, `codeDict_${new Date().getTime()}.xlsx`)
}

getList()
</script>
