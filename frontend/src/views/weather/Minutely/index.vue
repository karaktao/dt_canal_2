<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="${comment}" prop="timestamp">
        <el-date-picker clearable
          v-model="queryParams.timestamp"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择${comment}">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="${comment}" prop="temperature2m">
        <el-input
          v-model="queryParams.temperature2m"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="windSpeed10m">
        <el-input
          v-model="queryParams.windSpeed10m"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="precipitation">
        <el-input
          v-model="queryParams.precipitation"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="precipitationProbability">
        <el-input
          v-model="queryParams.precipitationProbability"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="visibility">
        <el-input
          v-model="queryParams.visibility"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="weatherCode">
        <el-input
          v-model="queryParams.weatherCode"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="locationLatitude">
        <el-input
          v-model="queryParams.locationLatitude"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="locationLongitude">
        <el-input
          v-model="queryParams.locationLongitude"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="elevation">
        <el-input
          v-model="queryParams.elevation"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="timezone">
        <el-input
          v-model="queryParams.timezone"
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
          v-hasPermi="['weather:Minutely:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['weather:Minutely:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['weather:Minutely:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['weather:Minutely:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="MinutelyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="${comment}" align="center" prop="id" />
      <el-table-column label="${comment}" align="center" prop="timestamp" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.timestamp, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="${comment}" align="center" prop="temperature2m" />
      <el-table-column label="${comment}" align="center" prop="windSpeed10m" />
      <el-table-column label="${comment}" align="center" prop="precipitation" />
      <el-table-column label="${comment}" align="center" prop="precipitationProbability" />
      <el-table-column label="${comment}" align="center" prop="visibility" />
      <el-table-column label="${comment}" align="center" prop="weatherCode" />
      <el-table-column label="${comment}" align="center" prop="locationLatitude" />
      <el-table-column label="${comment}" align="center" prop="locationLongitude" />
      <el-table-column label="${comment}" align="center" prop="elevation" />
      <el-table-column label="${comment}" align="center" prop="timezone" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['weather:Minutely:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['weather:Minutely:remove']">删除</el-button>
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

    <!-- 添加或修改weatherMinutely对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="MinutelyRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="${comment}" prop="timestamp">
          <el-date-picker clearable
            v-model="form.timestamp"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择${comment}">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="${comment}" prop="temperature2m">
          <el-input v-model="form.temperature2m" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="windSpeed10m">
          <el-input v-model="form.windSpeed10m" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="precipitation">
          <el-input v-model="form.precipitation" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="precipitationProbability">
          <el-input v-model="form.precipitationProbability" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="visibility">
          <el-input v-model="form.visibility" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="weatherCode">
          <el-input v-model="form.weatherCode" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="locationLatitude">
          <el-input v-model="form.locationLatitude" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="locationLongitude">
          <el-input v-model="form.locationLongitude" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="elevation">
          <el-input v-model="form.elevation" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="timezone">
          <el-input v-model="form.timezone" placeholder="请输入${comment}" />
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

<script setup name="Minutely">
import { listMinutely, getMinutely, delMinutely, addMinutely, updateMinutely } from "@/api/weather/Minutely"

const { proxy } = getCurrentInstance()

const MinutelyList = ref([])
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
    timestamp: null,
    temperature2m: null,
    windSpeed10m: null,
    precipitation: null,
    precipitationProbability: null,
    visibility: null,
    weatherCode: null,
    locationLatitude: null,
    locationLongitude: null,
    elevation: null,
    timezone: null
  },
  rules: {
    timestamp: [
      { required: true, message: "$comment不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询weatherMinutely列表 */
function getList() {
  loading.value = true
  listMinutely(queryParams.value).then(response => {
    MinutelyList.value = response.rows
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
    timestamp: null,
    temperature2m: null,
    windSpeed10m: null,
    precipitation: null,
    precipitationProbability: null,
    visibility: null,
    weatherCode: null,
    locationLatitude: null,
    locationLongitude: null,
    elevation: null,
    timezone: null
  }
  proxy.resetForm("MinutelyRef")
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
  title.value = "添加weatherMinutely"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getMinutely(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改weatherMinutely"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["MinutelyRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateMinutely(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addMinutely(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除weatherMinutely编号为"' + _ids + '"的数据项？').then(function() {
    return delMinutely(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('weather/Minutely/export', {
    ...queryParams.value
  }, `Minutely_${new Date().getTime()}.xlsx`)
}

getList()
</script>
