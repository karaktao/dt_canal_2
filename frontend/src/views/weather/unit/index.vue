<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="数据分类，如 daily, hourly, minutely_15, current" prop="category">
        <el-input
          v-model="queryParams.category"
          placeholder="请输入数据分类，如 daily, hourly, minutely_15, current"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="字段名，例如 temperature_2m" prop="field">
        <el-input
          v-model="queryParams.field"
          placeholder="请输入字段名，例如 temperature_2m"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单位，例如 ℃、mm" prop="unit">
        <el-input
          v-model="queryParams.unit"
          placeholder="请输入单位，例如 ℃、mm"
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
          v-hasPermi="['weather:unit:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['weather:unit:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['weather:unit:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['weather:unit:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="unitList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="${comment}" align="center" prop="id" />
      <el-table-column label="数据分类，如 daily, hourly, minutely_15, current" align="center" prop="category" />
      <el-table-column label="字段名，例如 temperature_2m" align="center" prop="field" />
      <el-table-column label="单位，例如 ℃、mm" align="center" prop="unit" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['weather:unit:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['weather:unit:remove']">删除</el-button>
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

    <!-- 添加或修改weatherUnit对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="unitRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="数据分类，如 daily, hourly, minutely_15, current" prop="category">
          <el-input v-model="form.category" placeholder="请输入数据分类，如 daily, hourly, minutely_15, current" />
        </el-form-item>
        <el-form-item label="字段名，例如 temperature_2m" prop="field">
          <el-input v-model="form.field" placeholder="请输入字段名，例如 temperature_2m" />
        </el-form-item>
        <el-form-item label="单位，例如 ℃、mm" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位，例如 ℃、mm" />
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

<script setup name="Unit">
import { listUnit, getUnit, delUnit, addUnit, updateUnit } from "@/api/weather/unit"

const { proxy } = getCurrentInstance()

const unitList = ref([])
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
    category: null,
    field: null,
    unit: null
  },
  rules: {
    category: [
      { required: true, message: "数据分类，如 daily, hourly, minutely_15, current不能为空", trigger: "blur" }
    ],
    field: [
      { required: true, message: "字段名，例如 temperature_2m不能为空", trigger: "blur" }
    ],
    unit: [
      { required: true, message: "单位，例如 ℃、mm不能为空", trigger: "blur" }
    ]
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询weatherUnit列表 */
function getList() {
  loading.value = true
  listUnit(queryParams.value).then(response => {
    unitList.value = response.rows
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
    id: null,
    category: null,
    field: null,
    unit: null
  }
  proxy.resetForm("unitRef")
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
  ids.value = selection.map(item => item.id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加weatherUnit"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getUnit(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改weatherUnit"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["unitRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateUnit(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addUnit(form.value).then(response => {
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
  const _ids = row.id || ids.value
  proxy.$modal.confirm('是否确认删除weatherUnit编号为"' + _ids + '"的数据项？').then(function() {
    return delUnit(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('weather/unit/export', {
    ...queryParams.value
  }, `unit_${new Date().getTime()}.xlsx`)
}

getList()
</script>
