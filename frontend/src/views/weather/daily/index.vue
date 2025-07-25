<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
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
      <el-form-item label="${comment}" prop="elevation">
        <el-input
          v-model="queryParams.elevation"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="weatherDate">
        <el-date-picker clearable
          v-model="queryParams.weatherDate"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择${comment}">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="${comment}" prop="temperatureMax">
        <el-input
          v-model="queryParams.temperatureMax"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="temperatureMin">
        <el-input
          v-model="queryParams.temperatureMin"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="windDirectionDominant">
        <el-input
          v-model="queryParams.windDirectionDominant"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="windSpeedMax">
        <el-input
          v-model="queryParams.windSpeedMax"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="precipitationSum">
        <el-input
          v-model="queryParams.precipitationSum"
          placeholder="请输入${comment}"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="${comment}" prop="precipitationProbabilityMax">
        <el-input
          v-model="queryParams.precipitationProbabilityMax"
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
          v-hasPermi="['weather:daily:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['weather:daily:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['weather:daily:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['weather:daily:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dailyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="${comment}" align="center" prop="id" />
      <el-table-column label="${comment}" align="center" prop="latitude" />
      <el-table-column label="${comment}" align="center" prop="longitude" />
      <el-table-column label="${comment}" align="center" prop="timezone" />
      <el-table-column label="${comment}" align="center" prop="utcOffsetSeconds" />
      <el-table-column label="${comment}" align="center" prop="elevation" />
      <el-table-column label="${comment}" align="center" prop="weatherDate" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.weatherDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="${comment}" align="center" prop="temperatureMax" />
      <el-table-column label="${comment}" align="center" prop="temperatureMin" />
      <el-table-column label="${comment}" align="center" prop="windDirectionDominant" />
      <el-table-column label="${comment}" align="center" prop="windSpeedMax" />
      <el-table-column label="${comment}" align="center" prop="precipitationSum" />
      <el-table-column label="${comment}" align="center" prop="precipitationProbabilityMax" />
      <el-table-column label="${comment}" align="center" prop="createdAt" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createdAt, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['weather:daily:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['weather:daily:remove']">删除</el-button>
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

    <!-- 添加或修改weatherDaily对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="dailyRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="${comment}" prop="latitude">
          <el-input v-model="form.latitude" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="longitude">
          <el-input v-model="form.longitude" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="timezone">
          <el-input v-model="form.timezone" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="utcOffsetSeconds">
          <el-input v-model="form.utcOffsetSeconds" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="elevation">
          <el-input v-model="form.elevation" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="weatherDate">
          <el-date-picker clearable
            v-model="form.weatherDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择${comment}">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="${comment}" prop="temperatureMax">
          <el-input v-model="form.temperatureMax" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="temperatureMin">
          <el-input v-model="form.temperatureMin" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="windDirectionDominant">
          <el-input v-model="form.windDirectionDominant" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="windSpeedMax">
          <el-input v-model="form.windSpeedMax" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="precipitationSum">
          <el-input v-model="form.precipitationSum" placeholder="请输入${comment}" />
        </el-form-item>
        <el-form-item label="${comment}" prop="precipitationProbabilityMax">
          <el-input v-model="form.precipitationProbabilityMax" placeholder="请输入${comment}" />
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

<script setup name="Daily">
import { listDaily, getDaily, delDaily, addDaily, updateDaily } from "@/api/weather/daily"

const { proxy } = getCurrentInstance()

const dailyList = ref([])
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
    latitude: null,
    longitude: null,
    timezone: null,
    utcOffsetSeconds: null,
    elevation: null,
    weatherDate: null,
    temperatureMax: null,
    temperatureMin: null,
    windDirectionDominant: null,
    windSpeedMax: null,
    precipitationSum: null,
    precipitationProbabilityMax: null,
    createdAt: null
  },
  rules: {
    latitude: [
      { required: true, message: "$comment不能为空", trigger: "blur" }
    ],
    longitude: [
      { required: true, message: "$comment不能为空", trigger: "blur" }
    ],
    weatherDate: [
      { required: true, message: "$comment不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询weatherDaily列表 */
function getList() {
  loading.value = true
  listDaily(queryParams.value).then(response => {
    dailyList.value = response.rows
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
    latitude: null,
    longitude: null,
    timezone: null,
    utcOffsetSeconds: null,
    elevation: null,
    weatherDate: null,
    temperatureMax: null,
    temperatureMin: null,
    windDirectionDominant: null,
    windSpeedMax: null,
    precipitationSum: null,
    precipitationProbabilityMax: null,
    createdAt: null
  }
  proxy.resetForm("dailyRef")
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
  title.value = "添加weatherDaily"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getDaily(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改weatherDaily"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["dailyRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updateDaily(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addDaily(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除weatherDaily编号为"' + _ids + '"的数据项？').then(function() {
    return delDaily(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('weather/daily/export', {
    ...queryParams.value
  }, `daily_${new Date().getTime()}.xlsx`)
}

getList()
</script>
