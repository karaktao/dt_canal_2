<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="数据生成版本号" prop="GeoGeneration">
        <el-input
          v-model="queryParams.GeoGeneration"
          placeholder="请输入数据生成版本号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="船闸名称" prop="Name">
        <el-input
          v-model="queryParams.Name"
          placeholder="请输入船闸名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属航道 ID" prop="FairwayId">
        <el-input
          v-model="queryParams.FairwayId"
          placeholder="请输入所属航道 ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属航道区段 ID" prop="FairwaySectionId">
        <el-input
          v-model="queryParams.FairwaySectionId"
          placeholder="请输入所属航道区段 ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属航线 ID" prop="RouteId">
        <el-input
          v-model="queryParams.RouteId"
          placeholder="请输入所属航线 ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="航线起点公里数" prop="RouteKmBegin">
        <el-input
          v-model="queryParams.RouteKmBegin"
          placeholder="请输入航线起点公里数"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="航线终点公里数" prop="RouteKmEnd">
        <el-input
          v-model="queryParams.RouteKmEnd"
          placeholder="请输入航线终点公里数"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否为航行计划参考点" prop="VoyagePlanningPoint">
        <el-input
          v-model="queryParams.VoyagePlanningPoint"
          placeholder="请输入是否为航行计划参考点"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="地址信息" prop="Address">
        <el-input
          v-model="queryParams.Address"
          placeholder="请输入地址信息"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="管理机构 ID" prop="AdministrationId">
        <el-input
          v-model="queryParams.AdministrationId"
          placeholder="请输入管理机构 ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属城市" prop="City">
        <el-input
          v-model="queryParams.City"
          placeholder="请输入所属城市"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="当前状态，如建成(CONSTRUCTED)" prop="Condition">
        <el-input
          v-model="queryParams.Condition"
          placeholder="请输入当前状态，如建成(CONSTRUCTED)"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="外部代码/识别符" prop="ForeignCode">
        <el-input
          v-model="queryParams.ForeignCode"
          placeholder="请输入外部代码/识别符"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否远程控制" prop="IsRemoteControlled">
        <el-input
          v-model="queryParams.IsRemoteControlled"
          placeholder="请输入是否远程控制"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="国际航道识别码 ISRS" prop="IsrsId">
        <el-input
          v-model="queryParams.IsrsId"
          placeholder="请输入国际航道识别码 ISRS"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="总长度" prop="Length">
        <el-input
          v-model="queryParams.Length"
          placeholder="请输入总长度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最大水位升高" prop="MaximumRise">
        <el-input
          v-model="queryParams.MaximumRise"
          placeholder="请输入最大水位升高"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="闸室数量" prop="NumberOfChambers">
        <el-input
          v-model="queryParams.NumberOfChambers"
          placeholder="请输入闸室数量"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作时间段 ID" prop="OperatingTimesId">
        <el-input
          v-model="queryParams.OperatingTimesId"
          placeholder="请输入操作时间段 ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系或操作电话" prop="PhoneNumber">
        <el-input
          v-model="queryParams.PhoneNumber"
          placeholder="请输入联系或操作电话"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="邮政编码" prop="PostalCode">
        <el-input
          v-model="queryParams.PostalCode"
          placeholder="请输入邮政编码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参考水位上界" prop="ReferenceLevelBeBu">
        <el-input
          v-model="queryParams.ReferenceLevelBeBu"
          placeholder="请输入参考水位上界"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="参考水位下界" prop="ReferenceLevelBoBi">
        <el-input
          v-model="queryParams.ReferenceLevelBoBi"
          placeholder="请输入参考水位下界"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="相关建筑名称" prop="RelatedBuildingComplexName">
        <el-input
          v-model="queryParams.RelatedBuildingComplexName"
          placeholder="请输入相关建筑名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="地图旋转角度" prop="Rotation">
        <el-input
          v-model="queryParams.Rotation"
          placeholder="请输入地图旋转角度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="锁闸 VIN 编码" prop="VinCode">
        <el-input
          v-model="queryParams.VinCode"
          placeholder="请输入锁闸 VIN 编码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="宽度" prop="Width">
        <el-input
          v-model="queryParams.Width"
          placeholder="请输入宽度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="记录创建时间" prop="CreatedAt">
        <el-date-picker clearable
          v-model="queryParams.CreatedAt"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择记录创建时间">
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
          v-hasPermi="['infrastructure:VNDSlock:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['infrastructure:VNDSlock:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['infrastructure:VNDSlock:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['infrastructure:VNDSlock:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="VNDSlockList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="唯一标识符，船闸 ID" align="center" prop="Id" />
      <el-table-column label="数据生成版本号" align="center" prop="GeoGeneration" />
      <el-table-column label="地理对象类型，如 lock" align="center" prop="GeoType" />
      <el-table-column label="几何信息，WKT 格式坐标" align="center" prop="Geometry" />
      <el-table-column label="船闸名称" align="center" prop="Name" />
      <el-table-column label="所属航道 ID" align="center" prop="FairwayId" />
      <el-table-column label="所属航道区段 ID" align="center" prop="FairwaySectionId" />
      <el-table-column label="所属航线 ID" align="center" prop="RouteId" />
      <el-table-column label="航线起点公里数" align="center" prop="RouteKmBegin" />
      <el-table-column label="航线终点公里数" align="center" prop="RouteKmEnd" />
      <el-table-column label="是否为航行计划参考点" align="center" prop="VoyagePlanningPoint" />
      <el-table-column label="地址信息" align="center" prop="Address" />
      <el-table-column label="管理机构 ID" align="center" prop="AdministrationId" />
      <el-table-column label="所属城市" align="center" prop="City" />
      <el-table-column label="当前状态，如建成(CONSTRUCTED)" align="center" prop="Condition" />
      <el-table-column label="外部代码/识别符" align="center" prop="ForeignCode" />
      <el-table-column label="是否远程控制" align="center" prop="IsRemoteControlled" />
      <el-table-column label="国际航道识别码 ISRS" align="center" prop="IsrsId" />
      <el-table-column label="总长度" align="center" prop="Length" />
      <el-table-column label="最大水位升高" align="center" prop="MaximumRise" />
      <el-table-column label="备注信息" align="center" prop="Note" />
      <el-table-column label="闸室数量" align="center" prop="NumberOfChambers" />
      <el-table-column label="操作时间段 ID" align="center" prop="OperatingTimesId" />
      <el-table-column label="联系或操作电话" align="center" prop="PhoneNumber" />
      <el-table-column label="邮政编码" align="center" prop="PostalCode" />
      <el-table-column label="参考水位上界" align="center" prop="ReferenceLevelBeBu" />
      <el-table-column label="参考水位下界" align="center" prop="ReferenceLevelBoBi" />
      <el-table-column label="相关建筑名称" align="center" prop="RelatedBuildingComplexName" />
      <el-table-column label="地图旋转角度" align="center" prop="Rotation" />
      <el-table-column label="锁闸 VIN 编码" align="center" prop="VinCode" />
      <el-table-column label="宽度" align="center" prop="Width" />
      <el-table-column label="记录创建时间" align="center" prop="CreatedAt" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.CreatedAt, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="${comment}" align="center" prop="GeoJSON" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['infrastructure:VNDSlock:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['infrastructure:VNDSlock:remove']">删除</el-button>
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

    <!-- 添加或修改VNDSlock对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="VNDSlockRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="数据生成版本号" prop="GeoGeneration">
          <el-input v-model="form.GeoGeneration" placeholder="请输入数据生成版本号" />
        </el-form-item>
        <el-form-item label="几何信息，WKT 格式坐标" prop="Geometry">
          <el-input v-model="form.Geometry" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="船闸名称" prop="Name">
          <el-input v-model="form.Name" placeholder="请输入船闸名称" />
        </el-form-item>
        <el-form-item label="所属航道 ID" prop="FairwayId">
          <el-input v-model="form.FairwayId" placeholder="请输入所属航道 ID" />
        </el-form-item>
        <el-form-item label="所属航道区段 ID" prop="FairwaySectionId">
          <el-input v-model="form.FairwaySectionId" placeholder="请输入所属航道区段 ID" />
        </el-form-item>
        <el-form-item label="所属航线 ID" prop="RouteId">
          <el-input v-model="form.RouteId" placeholder="请输入所属航线 ID" />
        </el-form-item>
        <el-form-item label="航线起点公里数" prop="RouteKmBegin">
          <el-input v-model="form.RouteKmBegin" placeholder="请输入航线起点公里数" />
        </el-form-item>
        <el-form-item label="航线终点公里数" prop="RouteKmEnd">
          <el-input v-model="form.RouteKmEnd" placeholder="请输入航线终点公里数" />
        </el-form-item>
        <el-form-item label="是否为航行计划参考点" prop="VoyagePlanningPoint">
          <el-input v-model="form.VoyagePlanningPoint" placeholder="请输入是否为航行计划参考点" />
        </el-form-item>
        <el-form-item label="地址信息" prop="Address">
          <el-input v-model="form.Address" placeholder="请输入地址信息" />
        </el-form-item>
        <el-form-item label="管理机构 ID" prop="AdministrationId">
          <el-input v-model="form.AdministrationId" placeholder="请输入管理机构 ID" />
        </el-form-item>
        <el-form-item label="所属城市" prop="City">
          <el-input v-model="form.City" placeholder="请输入所属城市" />
        </el-form-item>
        <el-form-item label="当前状态，如建成(CONSTRUCTED)" prop="Condition">
          <el-input v-model="form.Condition" placeholder="请输入当前状态，如建成(CONSTRUCTED)" />
        </el-form-item>
        <el-form-item label="外部代码/识别符" prop="ForeignCode">
          <el-input v-model="form.ForeignCode" placeholder="请输入外部代码/识别符" />
        </el-form-item>
        <el-form-item label="是否远程控制" prop="IsRemoteControlled">
          <el-input v-model="form.IsRemoteControlled" placeholder="请输入是否远程控制" />
        </el-form-item>
        <el-form-item label="国际航道识别码 ISRS" prop="IsrsId">
          <el-input v-model="form.IsrsId" placeholder="请输入国际航道识别码 ISRS" />
        </el-form-item>
        <el-form-item label="总长度" prop="Length">
          <el-input v-model="form.Length" placeholder="请输入总长度" />
        </el-form-item>
        <el-form-item label="最大水位升高" prop="MaximumRise">
          <el-input v-model="form.MaximumRise" placeholder="请输入最大水位升高" />
        </el-form-item>
        <el-form-item label="备注信息" prop="Note">
          <el-input v-model="form.Note" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="闸室数量" prop="NumberOfChambers">
          <el-input v-model="form.NumberOfChambers" placeholder="请输入闸室数量" />
        </el-form-item>
        <el-form-item label="操作时间段 ID" prop="OperatingTimesId">
          <el-input v-model="form.OperatingTimesId" placeholder="请输入操作时间段 ID" />
        </el-form-item>
        <el-form-item label="联系或操作电话" prop="PhoneNumber">
          <el-input v-model="form.PhoneNumber" placeholder="请输入联系或操作电话" />
        </el-form-item>
        <el-form-item label="邮政编码" prop="PostalCode">
          <el-input v-model="form.PostalCode" placeholder="请输入邮政编码" />
        </el-form-item>
        <el-form-item label="参考水位上界" prop="ReferenceLevelBeBu">
          <el-input v-model="form.ReferenceLevelBeBu" placeholder="请输入参考水位上界" />
        </el-form-item>
        <el-form-item label="参考水位下界" prop="ReferenceLevelBoBi">
          <el-input v-model="form.ReferenceLevelBoBi" placeholder="请输入参考水位下界" />
        </el-form-item>
        <el-form-item label="相关建筑名称" prop="RelatedBuildingComplexName">
          <el-input v-model="form.RelatedBuildingComplexName" placeholder="请输入相关建筑名称" />
        </el-form-item>
        <el-form-item label="地图旋转角度" prop="Rotation">
          <el-input v-model="form.Rotation" placeholder="请输入地图旋转角度" />
        </el-form-item>
        <el-form-item label="锁闸 VIN 编码" prop="VinCode">
          <el-input v-model="form.VinCode" placeholder="请输入锁闸 VIN 编码" />
        </el-form-item>
        <el-form-item label="宽度" prop="Width">
          <el-input v-model="form.Width" placeholder="请输入宽度" />
        </el-form-item>
        <el-form-item label="记录创建时间" prop="CreatedAt">
          <el-date-picker clearable
            v-model="form.CreatedAt"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择记录创建时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="${comment}" prop="GeoJSON">
          <el-input v-model="form.GeoJSON" type="textarea" placeholder="请输入内容" />
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

<script setup name="VNDSlock">
import { listVNDSlock, getVNDSlock, delVNDSlock, addVNDSlock, updateVNDSlock } from "@/api/infrastructure/VNDSlock"

const { proxy } = getCurrentInstance()

const VNDSlockList = ref([])
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
    GeoGeneration: null,
    GeoType: null,
    Geometry: null,
    Name: null,
    FairwayId: null,
    FairwaySectionId: null,
    RouteId: null,
    RouteKmBegin: null,
    RouteKmEnd: null,
    VoyagePlanningPoint: null,
    Address: null,
    AdministrationId: null,
    City: null,
    Condition: null,
    ForeignCode: null,
    IsRemoteControlled: null,
    IsrsId: null,
    Length: null,
    MaximumRise: null,
    Note: null,
    NumberOfChambers: null,
    OperatingTimesId: null,
    PhoneNumber: null,
    PostalCode: null,
    ReferenceLevelBeBu: null,
    ReferenceLevelBoBi: null,
    RelatedBuildingComplexName: null,
    Rotation: null,
    VinCode: null,
    Width: null,
    CreatedAt: null,
    GeoJSON: null
  },
  rules: {
    GeoGeneration: [
      { required: true, message: "数据生成版本号不能为空", trigger: "blur" }
    ],
    GeoType: [
      { required: true, message: "地理对象类型，如 lock不能为空", trigger: "change" }
    ],
    Geometry: [
      { required: true, message: "几何信息，WKT 格式坐标不能为空", trigger: "blur" }
    ],
    Name: [
      { required: true, message: "船闸名称不能为空", trigger: "blur" }
    ],
    FairwayId: [
      { required: true, message: "所属航道 ID不能为空", trigger: "blur" }
    ],
    FairwaySectionId: [
      { required: true, message: "所属航道区段 ID不能为空", trigger: "blur" }
    ],
    RouteId: [
      { required: true, message: "所属航线 ID不能为空", trigger: "blur" }
    ],
    RouteKmBegin: [
      { required: true, message: "航线起点公里数不能为空", trigger: "blur" }
    ],
    RouteKmEnd: [
      { required: true, message: "航线终点公里数不能为空", trigger: "blur" }
    ],
    VoyagePlanningPoint: [
      { required: true, message: "是否为航行计划参考点不能为空", trigger: "blur" }
    ],
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询VNDSlock列表 */
function getList() {
  loading.value = true
  listVNDSlock(queryParams.value).then(response => {
    VNDSlockList.value = response.rows
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
    Id: null,
    GeoGeneration: null,
    GeoType: null,
    Geometry: null,
    Name: null,
    FairwayId: null,
    FairwaySectionId: null,
    RouteId: null,
    RouteKmBegin: null,
    RouteKmEnd: null,
    VoyagePlanningPoint: null,
    Address: null,
    AdministrationId: null,
    City: null,
    Condition: null,
    ForeignCode: null,
    IsRemoteControlled: null,
    IsrsId: null,
    Length: null,
    MaximumRise: null,
    Note: null,
    NumberOfChambers: null,
    OperatingTimesId: null,
    PhoneNumber: null,
    PostalCode: null,
    ReferenceLevelBeBu: null,
    ReferenceLevelBoBi: null,
    RelatedBuildingComplexName: null,
    Rotation: null,
    VinCode: null,
    Width: null,
    CreatedAt: null,
    GeoJSON: null
  }
  proxy.resetForm("VNDSlockRef")
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
  ids.value = selection.map(item => item.Id)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 新增按钮操作 */
function handleAdd() {
  reset()
  open.value = true
  title.value = "添加VNDSlock"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _Id = row.Id || ids.value
  getVNDSlock(_Id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改VNDSlock"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["VNDSlockRef"].validate(valid => {
    if (valid) {
      if (form.value.Id != null) {
        updateVNDSlock(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addVNDSlock(form.value).then(response => {
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
  const _Ids = row.Id || ids.value
  proxy.$modal.confirm('是否确认删除VNDSlock编号为"' + _Ids + '"的数据项？').then(function() {
    return delVNDSlock(_Ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('infrastructure/VNDSlock/export', {
    ...queryParams.value
  }, `VNDSlock_${new Date().getTime()}.xlsx`)
}

getList()
</script>
