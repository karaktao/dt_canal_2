<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="${comment}" prop="recordTime">
        <el-date-picker clearable
          v-model="queryParams.recordTime"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择${comment}">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="数据时间间隔 (秒)" prop="intervalSeconds">
        <el-input
          v-model="queryParams.intervalSeconds"
          placeholder="请输入数据时间间隔 (秒)"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单位: °C" prop="temperature2m">
        <el-input
          v-model="queryParams.temperature2m"
          placeholder="请输入单位: °C"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单位: km/h" prop="windSpeed10m">
        <el-input
          v-model="queryParams.windSpeed10m"
          placeholder="请输入单位: km/h"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单位: °" prop="windDirection10m">
        <el-input
          v-model="queryParams.windDirection10m"
          placeholder="请输入单位: °"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单位: km/h" prop="windGusts10m">
        <el-input
          v-model="queryParams.windGusts10m"
          placeholder="请输入单位: km/h"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单位: mm" prop="rain">
        <el-input
          v-model="queryParams.rain"
          placeholder="请输入单位: mm"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单位: %" prop="relativeHumidity2m">
        <el-input
          v-model="queryParams.relativeHumidity2m"
          placeholder="请输入单位: %"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="WMO 天气代码" prop="weatherCode">
        <el-input
          v-model="queryParams.weatherCode"
          placeholder="请输入WMO 天气代码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="latitude">
        <el-input
          v-model="queryParams.latitude"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="longitude">
        <el-input
          v-model="queryParams.longitude"
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
      <el-form-item label="${comment}" prop="utcOffsetSeconds">
        <el-input
          v-model="queryParams.utcOffsetSeconds"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="createdAt">
        <el-date-picker clearable
          v-model="queryParams.createdAt"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择${comment}">
        </el-date-picker>
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
          v-hasPermi="['weather:current:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['weather:current:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['weather:current:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['weather:current:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="currentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="${comment}" align="center" prop="id" />
      <el-table-column label="${comment}" align="center" prop="recordTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.recordTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="数据时间间隔 (秒)" align="center" prop="intervalSeconds" />
      <el-table-column label="单位: °C" align="center" prop="temperature2m" />
      <el-table-column label="单位: km/h" align="center" prop="windSpeed10m" />
      <el-table-column label="单位: °" align="center" prop="windDirection10m" />
      <el-table-column label="单位: km/h" align="center" prop="windGusts10m" />
      <el-table-column label="单位: mm" align="center" prop="rain" />
      <el-table-column label="单位: %" align="center" prop="relativeHumidity2m" />
      <el-table-column label="WMO 天气代码" align="center" prop="weatherCode" />
      <el-table-column label="${comment}" align="center" prop="latitude" />
      <el-table-column label="${comment}" align="center" prop="longitude" />
      <el-table-column label="${comment}" align="center" prop="elevation" />
      <el-table-column label="${comment}" align="center" prop="timezone" />
      <el-table-column label="${comment}" align="center" prop="utcOffsetSeconds" />
      <el-table-column label="${comment}" align="center" prop="createdAt" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createdAt, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['weather:current:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['weather:current:remove']">删除</el-button>
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

    <!-- 添加或修改weatherCurrent对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="currentRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="${comment}" prop="recordTime">
          <el-date-picker clearable
            v-model="form.recordTime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择${comment}">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="数据时间间隔 (秒)" prop="intervalSeconds">
          <el-input v-model="form.intervalSeconds" placeholder="请输入数据时间间隔 (秒)" />
        </el-form-item>
        <el-form-item label="单位: °C" prop="temperature2m">
          <el-input v-model="form.temperature2m" placeholder="请输入单位: °C" />
        </el-form-item>
        <el-form-item label="单位: km/h" prop="windSpeed10m">
          <el-input v-model="form.windSpeed10m" placeholder="请输入单位: km/h" />
        </el-form-item>
        <el-form-item label="单位: °" prop="windDirection10m">
          <el-input v-model="form.windDirection10m" placeholder="请输入单位: °" />
        </el-form-item>
        <el-form-item label="单位: km/h" prop="windGusts10m">
          <el-input v-model="form.windGusts10m" placeholder="请输入单位: km/h" />
        </el-form-item>
        <el-form-item label="单位: mm" prop="rain">
          <el-input v-model="form.rain" placeholder="请输入单位: mm" />
        </el-form-item>
        <el-form-item label="单位: %" prop="relativeHumidity2m">
          <el-input v-model="form.relativeHumidity2m" placeholder="请输入单位: %" />
        </el-form-item>
        <el-form-item label="WMO 天气代码" prop="weatherCode">
          <el-input v-model="form.weatherCode" placeholder="请输入WMO 天气代码" />
        </el-form-item>
        <el-form-item label="${comment}" prop="latitude">
          <el-input v-model="form.latitude" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="longitude">
          <el-input v-model="form.longitude" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="elevation">
          <el-input v-model="form.elevation" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="timezone">
          <el-input v-model="form.timezone" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="utcOffsetSeconds">
          <el-input v-model="form.utcOffsetSeconds" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="createdAt">
          <el-date-picker clearable
            v-model="form.createdAt"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择${comment}">
          </el-date-picker>
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

<script setup name="Current">
import { listCurrent, getCurrent, delCurrent, addCurrent, updateCurrent } from "@/api/weather/current"

const { proxy } = getCurrentInstance()

const currentList = ref([])
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
    recordTime: null,
    intervalSeconds: null,
    temperature2m: null,
    windSpeed10m: null,
    windDirection10m: null,
    windGusts10m: null,
    rain: null,
    relativeHumidity2m: null,
    weatherCode: null,
    latitude: null,
    longitude: null,
    elevation: null,
    timezone: null,
    utcOffsetSeconds: null,
    createdAt: null
  },
  rules: {
    recordTime: [
      { required: true, message: "$comment不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询weatherCurrent列表 */
function getList() {
  loading.value = true
  listCurrent(queryParams.value).then(response => {
    currentList.value = response.rows
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
    recordTime: null,
    intervalSeconds: null,
    temperature2m: null,
    windSpeed10m: null,
    windDirection10m: null,
    windGusts10m: null,
    rain: null,
    relativeHumidity2m: null,
    weatherCode: null,
    latitude: null,
    longitude: null,
    elevation: null,
    timezone: null,
    utcOffsetSeconds: null,
    createdAt: null
  }
  proxy.resetForm("currentRef")
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
  title.value = "添加weatherCurrent"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getCurrent(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改weatherCurrent"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["currentRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateCurrent(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addCurrent(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除weatherCurrent编号为"' + _ids + '"的数据项？').then(function() {
    return delCurrent(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('weather/current/export', {
    ...queryParams.value
  }, `current_${new Date().getTime()}.xlsx`)
}

getList()
</script>
