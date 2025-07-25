<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="所属租户ID" prop="tenantId">
        <el-input
          v-model="queryParams.tenantId"
          placeholder="请输入所属租户ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所需吨位" prop="tonnageDemand">
        <el-input
          v-model="queryParams.tonnageDemand"
          placeholder="请输入所需吨位"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所需体积" prop="volumeDemand">
        <el-input
          v-model="queryParams.volumeDemand"
          placeholder="请输入所需体积"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所需集装箱数" prop="containerDemand">
        <el-input
          v-model="queryParams.containerDemand"
          placeholder="请输入所需集装箱数"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="船舶ID" prop="vesselId">
        <el-input
          v-model="queryParams.vesselId"
          placeholder="请输入船舶ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="MMSI编号" prop="mmsiNumber">
        <el-input
          v-model="queryParams.mmsiNumber"
          placeholder="请输入MMSI编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="船名" prop="vesselName">
        <el-input
          v-model="queryParams.vesselName"
          placeholder="请输入船名"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="可用吨位" prop="tonnageAvailable">
        <el-input
          v-model="queryParams.tonnageAvailable"
          placeholder="请输入可用吨位"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="可用体积" prop="volumeAvailable">
        <el-input
          v-model="queryParams.volumeAvailable"
          placeholder="请输入可用体积"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="可用集装箱数" prop="containerAvailable">
        <el-input
          v-model="queryParams.containerAvailable"
          placeholder="请输入可用集装箱数"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否返程" prop="isReturnTripAvailable">
        <el-select v-model="queryParams.isReturnTripAvailable" placeholder="请选择是否返程" clearable>
          <el-option
            v-for="dict in sys_yes_no"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="船舶可用时间段" prop="vesselAvailabilityStart">
        <el-date-picker clearable
          v-model="queryParams.vesselAvailabilityStart"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择船舶可用时间段">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="船舶可用时间段" prop="vesselAvailabilityEnd">
        <el-date-picker clearable
          v-model="queryParams.vesselAvailabilityEnd"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择船舶可用时间段">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="到达港口" prop="returnDestinationPort">
        <el-input
          v-model="queryParams.returnDestinationPort"
          placeholder="请输入到达港口"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="到达港口" prop="returnDestinationPortId">
        <el-input
          v-model="queryParams.returnDestinationPortId"
          placeholder="请输入到达港口"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="出发港口" prop="originPort">
        <el-input
          v-model="queryParams.originPort"
          placeholder="请输入出发港口"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="出发港口" prop="originPortId">
        <el-input
          v-model="queryParams.originPortId"
          placeholder="请输入出发港口"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="到达港口" prop="destinationPort">
        <el-input
          v-model="queryParams.destinationPort"
          placeholder="请输入到达港口"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="到达港口" prop="destinationPortId">
        <el-input
          v-model="queryParams.destinationPortId"
          placeholder="请输入到达港口"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="出发时间起" prop="departureStart">
        <el-date-picker clearable
          v-model="queryParams.departureStart"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择出发时间起">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="出发时间止" prop="departureEnd">
        <el-date-picker clearable
          v-model="queryParams.departureEnd"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择出发时间止">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="到达时间起" prop="arrivalStart">
        <el-date-picker clearable
          v-model="queryParams.arrivalStart"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择到达时间起">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="到达时间止" prop="arrivalEnd">
        <el-date-picker clearable
          v-model="queryParams.arrivalEnd"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择到达时间止">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="预计到达时间" style="width: 308px">
        <el-date-picker
          v-model="daterangeArrivalEstimate"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="装货时间" style="width: 308px">
        <el-date-picker
          v-model="daterangeUploadTime"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="卸货时间" style="width: 308px">
        <el-date-picker
          v-model="daterangeUnloadTime"
          value-format="YYYY-MM-DD"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="经停港口" prop="intermediatePortsId">
        <el-input
          v-model="queryParams.intermediatePortsId"
          placeholder="请输入经停港口"
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
          v-hasPermi="['transport:publish:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['transport:publish:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['transport:publish:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['transport:publish:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="publishList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="id" />
      <el-table-column label="所属租户ID" align="center" prop="tenantId" />
      <el-table-column label="指派类型" align="center" prop="assignmentType" />
      <el-table-column label="货物类型" align="center" prop="cargoType" />
      <el-table-column label="所需吨位" align="center" prop="tonnageDemand" />
      <el-table-column label="所需体积" align="center" prop="volumeDemand" />
      <el-table-column label="所需集装箱数" align="center" prop="containerDemand" />
      <el-table-column label="船舶ID" align="center" prop="vesselId" />
      <el-table-column label="MMSI编号" align="center" prop="mmsiNumber" />
      <el-table-column label="船名" align="center" prop="vesselName" />
      <el-table-column label="是否空船" align="center" prop="isEmptyVessel" />
      <el-table-column label="可用吨位" align="center" prop="tonnageAvailable" />
      <el-table-column label="可用体积" align="center" prop="volumeAvailable" />
      <el-table-column label="可用集装箱数" align="center" prop="containerAvailable" />
      <el-table-column label="是否返程" align="center" prop="isReturnTripAvailable">
        <template #default="scope">
          <dict-tag :options="sys_yes_no" :value="scope.row.isReturnTripAvailable"/>
        </template>
      </el-table-column>
      <el-table-column label="船舶可用时间段" align="center" prop="vesselAvailabilityStart" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.vesselAvailabilityStart, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="船舶可用时间段" align="center" prop="vesselAvailabilityEnd" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.vesselAvailabilityEnd, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="到达港口" align="center" prop="returnDestinationPort" />
      <el-table-column label="到达港口" align="center" prop="returnDestinationPortId" />
      <el-table-column label="出发港口" align="center" prop="originPort" />
      <el-table-column label="出发港口" align="center" prop="originPortId" />
      <el-table-column label="到达港口" align="center" prop="destinationPort" />
      <el-table-column label="到达港口" align="center" prop="destinationPortId" />
      <el-table-column label="出发时间起" align="center" prop="departureStart" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.departureStart, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="出发时间止" align="center" prop="departureEnd" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.departureEnd, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="到达时间起" align="center" prop="arrivalStart" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.arrivalStart, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="到达时间止" align="center" prop="arrivalEnd" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.arrivalEnd, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="预计到达时间" align="center" prop="arrivalEstimate" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.arrivalEstimate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="装货时间" align="center" prop="uploadTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.uploadTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="卸货时间" align="center" prop="unloadTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.unloadTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="优先级设定" align="center" prop="prioritySetting" />
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="经停港口" align="center" prop="intermediatePorts" />
      <el-table-column label="经停港口" align="center" prop="intermediatePortsId" />
      <el-table-column label="货物特性" align="center" prop="cargoProperty" />
      <el-table-column label="" align="center" prop="isMerge" />
      <el-table-column label="" align="center" prop="isTransshipment" />
      <el-table-column label="${comment}" align="center" prop="containerId" />
      <el-table-column label="${comment}" align="center" prop="remarks" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['transport:publish:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['transport:publish:remove']">删除</el-button>
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

    <!-- 添加或修改物流发布对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="publishRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="所需吨位" prop="tonnageDemand">
          <el-input v-model="form.tonnageDemand" placeholder="请输入所需吨位" />
        </el-form-item>
        <el-form-item label="所需体积" prop="volumeDemand">
          <el-input v-model="form.volumeDemand" placeholder="请输入所需体积" />
        </el-form-item>
        <el-form-item label="所需集装箱数" prop="containerDemand">
          <el-input v-model="form.containerDemand" placeholder="请输入所需集装箱数" />
        </el-form-item>
        <el-form-item label="MMSI编号" prop="mmsiNumber">
          <el-input v-model="form.mmsiNumber" placeholder="请输入MMSI编号" />
        </el-form-item>
        <el-form-item label="船名" prop="vesselName">
          <el-input v-model="form.vesselName" placeholder="请输入船名" />
        </el-form-item>
        <el-form-item label="可用吨位" prop="tonnageAvailable">
          <el-input v-model="form.tonnageAvailable" placeholder="请输入可用吨位" />
        </el-form-item>
        <el-form-item label="可用体积" prop="volumeAvailable">
          <el-input v-model="form.volumeAvailable" placeholder="请输入可用体积" />
        </el-form-item>
        <el-form-item label="可用集装箱数" prop="containerAvailable">
          <el-input v-model="form.containerAvailable" placeholder="请输入可用集装箱数" />
        </el-form-item>
        <el-form-item label="是否返程" prop="isReturnTripAvailable">
          <el-select v-model="form.isReturnTripAvailable" placeholder="请选择是否返程">
            <el-option
              v-for="dict in sys_yes_no"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="船舶可用时间段" prop="vesselAvailabilityStart">
          <el-date-picker clearable
            v-model="form.vesselAvailabilityStart"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择船舶可用时间段">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="船舶可用时间段" prop="vesselAvailabilityEnd">
          <el-date-picker clearable
            v-model="form.vesselAvailabilityEnd"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择船舶可用时间段">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="到达港口" prop="returnDestinationPort">
          <el-input v-model="form.returnDestinationPort" placeholder="请输入到达港口" />
        </el-form-item>
        <el-form-item label="到达港口" prop="returnDestinationPortId">
          <el-input v-model="form.returnDestinationPortId" placeholder="请输入到达港口" />
        </el-form-item>
        <el-form-item label="出发港口" prop="originPort">
          <el-input v-model="form.originPort" placeholder="请输入出发港口" />
        </el-form-item>
        <el-form-item label="到达港口" prop="destinationPort">
          <el-input v-model="form.destinationPort" placeholder="请输入到达港口" />
        </el-form-item>
        <el-form-item label="出发时间起" prop="departureStart">
          <el-date-picker clearable
            v-model="form.departureStart"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择出发时间起">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="出发时间止" prop="departureEnd">
          <el-date-picker clearable
            v-model="form.departureEnd"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择出发时间止">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="到达时间起" prop="arrivalStart">
          <el-date-picker clearable
            v-model="form.arrivalStart"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择到达时间起">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="到达时间止" prop="arrivalEnd">
          <el-date-picker clearable
            v-model="form.arrivalEnd"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择到达时间止">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="预计到达时间" prop="arrivalEstimate">
          <el-date-picker clearable
            v-model="form.arrivalEstimate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择预计到达时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="装货时间" prop="uploadTime">
          <el-date-picker clearable
            v-model="form.uploadTime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择装货时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="卸货时间" prop="unloadTime">
          <el-date-picker clearable
            v-model="form.unloadTime"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择卸货时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="${comment}" prop="remarks">
          <el-input v-model="form.remarks" placeholder="请输入${comment}" />
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

<script setup name="Publish">
import { listPublish, getPublish, delPublish, addPublish, updatePublish } from "@/api/transport/publish"

const { proxy } = getCurrentInstance()
const { sys_yes_no } = proxy.useDict('sys_yes_no')

const publishList = ref([])
const open = ref(false)
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const title = ref("")
const daterangeArrivalEstimate = ref([])
const daterangeUploadTime = ref([])
const daterangeUnloadTime = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    tenantId: null,
    assignmentType: 'vessel_to_cargo',
    cargoType: null,
    tonnageDemand: null,
    volumeDemand: null,
    containerDemand: null,
    vesselId: null,
    mmsiNumber: null,
    vesselName: null,
    isEmptyVessel: null,
    tonnageAvailable: null,
    volumeAvailable: null,
    containerAvailable: null,
    isReturnTripAvailable: null,
    vesselAvailabilityStart: null,
    vesselAvailabilityEnd: null,
    returnDestinationPort: null,
    returnDestinationPortId: null,
    originPort: null,
    originPortId: null,
    destinationPort: null,
    destinationPortId: null,
    departureStart: null,
    departureEnd: null,
    arrivalStart: null,
    arrivalEnd: null,
    arrivalEstimate: null,
    uploadTime: null,
    unloadTime: null,
    prioritySetting: null,
    status: null,
    intermediatePorts: null,
    intermediatePortsId: null,
    isMerge: null,
    isTransshipment: null,
  },
  rules: {
    originPort: [
      { required: true, message: "出发港口不能为空", trigger: "blur" }
    ],
    destinationPort: [
      { required: true, message: "到达港口不能为空", trigger: "blur" }
    ],
    departureStart: [
      { required: true, message: "出发时间起不能为空", trigger: "blur" }
    ],
    arrivalStart: [
      { required: true, message: "到达时间起不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询物流发布列表 */
function getList() {
  loading.value = true
  queryParams.value.params = {}
  if (null != daterangeArrivalEstimate && '' != daterangeArrivalEstimate) {
    queryParams.value.params["beginArrivalEstimate"] = daterangeArrivalEstimate.value[0]
    queryParams.value.params["endArrivalEstimate"] = daterangeArrivalEstimate.value[1]
  }
  if (null != daterangeUploadTime && '' != daterangeUploadTime) {
    queryParams.value.params["beginUploadTime"] = daterangeUploadTime.value[0]
    queryParams.value.params["endUploadTime"] = daterangeUploadTime.value[1]
  }
  if (null != daterangeUnloadTime && '' != daterangeUnloadTime) {
    queryParams.value.params["beginUnloadTime"] = daterangeUnloadTime.value[0]
    queryParams.value.params["endUnloadTime"] = daterangeUnloadTime.value[1]
  }
  listPublish(queryParams.value).then(response => {
    publishList.value = response.rows
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
    tenantId: null,
    assignmentType: 'vessel_to_cargo',
    publishedBy: null,
    publishedAt: null,
    createdAt: null,
    assignedAt: null,
    cargoType: null,
    tonnageDemand: null,
    volumeDemand: null,
    containerDemand: null,
    vesselId: null,
    mmsiNumber: null,
    vesselName: null,
    isEmptyVessel: null,
    tonnageAvailable: null,
    volumeAvailable: null,
    containerAvailable: null,
    isReturnTripAvailable: null,
    vesselAvailabilityStart: null,
    vesselAvailabilityEnd: null,
    returnDestinationPort: null,
    returnDestinationPortId: null,
    originPort: null,
    originPortId: null,
    destinationPort: null,
    destinationPortId: null,
    departureStart: null,
    departureEnd: null,
    arrivalStart: null,
    arrivalEnd: null,
    arrivalEstimate: null,
    uploadTime: null,
    unloadTime: null,
    prioritySetting: null,
    status: null,
    intermediatePorts: null,
    geoPath: null,
    intermediatePortsId: null,
    cargoProperty: null,
    isMerge: null,
    isTransshipment: null,
    containerId: null,
    remarks: null
  }
  proxy.resetForm("publishRef")
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  daterangeArrivalEstimate.value = []
  daterangeUploadTime.value = []
  daterangeUnloadTime.value = []
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
  title.value = "添加物流发布"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _id = row.id || ids.value
  getPublish(_id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改物流发布"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["publishRef"].validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        updatePublish(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addPublish(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除物流发布编号为"' + _ids + '"的数据项？').then(function() {
    return delPublish(_ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('transport/publish/export', {
    ...queryParams.value
  }, `publish_${new Date().getTime()}.xlsx`)
}

getList()
</script>
