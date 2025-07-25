<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="地理版本号" prop="GeoGeneration">
        <el-input
          v-model="queryParams.GeoGeneration"
          placeholder="请输入地理版本号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="名称" prop="Name">
        <el-input
          v-model="queryParams.Name"
          placeholder="请输入名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="VIN码" prop="VinCode">
        <el-input
          v-model="queryParams.VinCode"
          placeholder="请输入VIN码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联合国地点代码" prop="UnLocationCode">
        <el-input
          v-model="queryParams.UnLocationCode"
          placeholder="请输入联合国地点代码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="航线ID" prop="RouteId">
        <el-input
          v-model="queryParams.RouteId"
          placeholder="请输入航线ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="起点公里数" prop="RouteKmBegin">
        <el-input
          v-model="queryParams.RouteKmBegin"
          placeholder="请输入起点公里数"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="终点公里数" prop="RouteKmEnd">
        <el-input
          v-model="queryParams.RouteKmEnd"
          placeholder="请输入终点公里数"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="城市" prop="City">
        <el-input
          v-model="queryParams.City"
          placeholder="请输入城市"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="行政机构ID" prop="AdministrationId">
        <el-input
          v-model="queryParams.AdministrationId"
          placeholder="请输入行政机构ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="航道段ID" prop="FairwaySectionId">
        <el-input
          v-model="queryParams.FairwaySectionId"
          placeholder="请输入航道段ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="航道ID" prop="FairwayId">
        <el-input
          v-model="queryParams.FairwayId"
          placeholder="请输入航道ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="ISRS ID" prop="IsrsId">
        <el-input
          v-model="queryParams.IsrsId"
          placeholder="请输入ISRS ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状况" prop="Condition">
        <el-input
          v-model="queryParams.Condition"
          placeholder="请输入状况"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分类" prop="Category">
        <el-input
          v-model="queryParams.Category"
          placeholder="请输入分类"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="宽度方向" prop="WidthPosition">
        <el-input
          v-model="queryParams.WidthPosition"
          placeholder="请输入宽度方向"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否有起重机" prop="HasCrane">
        <el-input
          v-model="queryParams.HasCrane"
          placeholder="请输入是否有起重机"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="终端ID" prop="TerminalId">
        <el-input
          v-model="queryParams.TerminalId"
          placeholder="请输入终端ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="地址" prop="Address">
        <el-input
          v-model="queryParams.Address"
          placeholder="请输入地址"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="社区" prop="Community">
        <el-input
          v-model="queryParams.Community"
          placeholder="请输入社区"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="可达性" prop="Accessibility">
        <el-input
          v-model="queryParams.Accessibility"
          placeholder="请输入可达性"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="可用高度" prop="AvailableHeight">
        <el-input
          v-model="queryParams.AvailableHeight"
          placeholder="请输入可用高度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="可用长度" prop="AvailableLength">
        <el-input
          v-model="queryParams.AvailableLength"
          placeholder="请输入可用长度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="可用宽度" prop="AvailableWidth">
        <el-input
          v-model="queryParams.AvailableWidth"
          placeholder="请输入可用宽度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="建筑材料" prop="ConstructionMaterial">
        <el-input
          v-model="queryParams.ConstructionMaterial"
          placeholder="请输入建筑材料"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="外部编码" prop="ForeignCode">
        <el-input
          v-model="queryParams.ForeignCode"
          placeholder="请输入外部编码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="泊位长度" prop="Length">
        <el-input
          v-model="queryParams.Length"
          placeholder="请输入泊位长度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最大船舶长度" prop="MaximalShipLength">
        <el-input
          v-model="queryParams.MaximalShipLength"
          placeholder="请输入最大船舶长度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最大船队高度" prop="MaximumConvoyHeight">
        <el-input
          v-model="queryParams.MaximumConvoyHeight"
          placeholder="请输入最大船队高度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最大船队长度" prop="MaximumConvoyLength">
        <el-input
          v-model="queryParams.MaximumConvoyLength"
          placeholder="请输入最大船队长度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最大船队宽度" prop="MaximumConvoyWidth">
        <el-input
          v-model="queryParams.MaximumConvoyWidth"
          placeholder="请输入最大船队宽度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最大吃水深度" prop="MaximumDraught">
        <el-input
          v-model="queryParams.MaximumDraught"
          placeholder="请输入最大吃水深度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最大船舶高度" prop="MaximumShipHeight">
        <el-input
          v-model="queryParams.MaximumShipHeight"
          placeholder="请输入最大船舶高度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最大船舶宽度" prop="MaximumShipWidth">
        <el-input
          v-model="queryParams.MaximumShipWidth"
          placeholder="请输入最大船舶宽度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最大停泊时间" prop="MaximumStayDurationH">
        <el-input
          v-model="queryParams.MaximumStayDurationH"
          placeholder="请输入最大停泊时间"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最小船舶长度" prop="MinimalShipLength">
        <el-input
          v-model="queryParams.MinimalShipLength"
          placeholder="请输入最小船舶长度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="系泊设施描述" prop="MooringFacilitiesDesc">
        <el-input
          v-model="queryParams.MooringFacilitiesDesc"
          placeholder="请输入系泊设施描述"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="系泊设施数量" prop="NumberOfMooringFacilities">
        <el-input
          v-model="queryParams.NumberOfMooringFacilities"
          placeholder="请输入系泊设施数量"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="泊位排数" prop="NumberOfRows">
        <el-input
          v-model="queryParams.NumberOfRows"
          placeholder="请输入泊位排数"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="港口区域ID" prop="PortAreaId">
        <el-input
          v-model="queryParams.PortAreaId"
          placeholder="请输入港口区域ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="码头质量" prop="QuayQuality">
        <el-input
          v-model="queryParams.QuayQuality"
          placeholder="请输入码头质量"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="旋转角度" prop="Rotation">
        <el-input
          v-model="queryParams.Rotation"
          placeholder="请输入旋转角度"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="岸线类型" prop="ShorelineCategory">
        <el-input
          v-model="queryParams.ShorelineCategory"
          placeholder="请输入岸线类型"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="信号标志" prop="Signaling">
        <el-input
          v-model="queryParams.Signaling"
          placeholder="请输入信号标志"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="信号标志" prop="Signalling">
        <el-input
          v-model="queryParams.Signalling"
          placeholder="请输入信号标志"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="运输可能描述" prop="TransportPossibilitiesDesc">
        <el-input
          v-model="queryParams.TransportPossibilitiesDesc"
          placeholder="请输入运输可能描述"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="泊位宽度" prop="Width">
        <el-input
          v-model="queryParams.Width"
          placeholder="请输入泊位宽度"
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
          v-hasPermi="['infrastructure:VNDSberth:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="Edit"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['infrastructure:VNDSberth:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="Delete"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['infrastructure:VNDSberth:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
          v-hasPermi="['infrastructure:VNDSberth:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="VNDSberthList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="唯一ID" align="center" prop="Id" />
      <el-table-column label="地理类型" align="center" prop="GeoType" />
      <el-table-column label="地理版本号" align="center" prop="GeoGeneration" />
      <el-table-column label="${comment}" align="center" prop="GeoJSON" />
      <el-table-column label="名称" align="center" prop="Name" />
      <el-table-column label="几何信息" align="center" prop="Geometry" />
      <el-table-column label="VIN码" align="center" prop="VinCode" />
      <el-table-column label="联合国地点代码" align="center" prop="UnLocationCode" />
      <el-table-column label="备注" align="center" prop="Note" />
      <el-table-column label="航线ID" align="center" prop="RouteId" />
      <el-table-column label="起点公里数" align="center" prop="RouteKmBegin" />
      <el-table-column label="终点公里数" align="center" prop="RouteKmEnd" />
      <el-table-column label="城市" align="center" prop="City" />
      <el-table-column label="行政机构ID" align="center" prop="AdministrationId" />
      <el-table-column label="航道段ID" align="center" prop="FairwaySectionId" />
      <el-table-column label="航道ID" align="center" prop="FairwayId" />
      <el-table-column label="ISRS ID" align="center" prop="IsrsId" />
      <el-table-column label="状况" align="center" prop="Condition" />
      <el-table-column label="分类" align="center" prop="Category" />
      <el-table-column label="宽度方向" align="center" prop="WidthPosition" />
      <el-table-column label="是否有起重机" align="center" prop="HasCrane" />
      <el-table-column label="终端ID" align="center" prop="TerminalId" />
      <el-table-column label="地址" align="center" prop="Address" />
      <el-table-column label="社区" align="center" prop="Community" />
      <el-table-column label="可达性" align="center" prop="Accessibility" />
      <el-table-column label="可用高度" align="center" prop="AvailableHeight" />
      <el-table-column label="可用长度" align="center" prop="AvailableLength" />
      <el-table-column label="可用宽度" align="center" prop="AvailableWidth" />
      <el-table-column label="建筑材料" align="center" prop="ConstructionMaterial" />
      <el-table-column label="外部编码" align="center" prop="ForeignCode" />
      <el-table-column label="泊位长度" align="center" prop="Length" />
      <el-table-column label="最大船舶长度" align="center" prop="MaximalShipLength" />
      <el-table-column label="最大船队高度" align="center" prop="MaximumConvoyHeight" />
      <el-table-column label="最大船队长度" align="center" prop="MaximumConvoyLength" />
      <el-table-column label="最大船队宽度" align="center" prop="MaximumConvoyWidth" />
      <el-table-column label="最大吃水深度" align="center" prop="MaximumDraught" />
      <el-table-column label="最大船舶高度" align="center" prop="MaximumShipHeight" />
      <el-table-column label="最大船舶宽度" align="center" prop="MaximumShipWidth" />
      <el-table-column label="最大停泊时间" align="center" prop="MaximumStayDurationH" />
      <el-table-column label="最小船舶长度" align="center" prop="MinimalShipLength" />
      <el-table-column label="系泊设施描述" align="center" prop="MooringFacilitiesDesc" />
      <el-table-column label="系泊设施数量" align="center" prop="NumberOfMooringFacilities" />
      <el-table-column label="泊位排数" align="center" prop="NumberOfRows" />
      <el-table-column label="港口区域ID" align="center" prop="PortAreaId" />
      <el-table-column label="码头质量" align="center" prop="QuayQuality" />
      <el-table-column label="旋转角度" align="center" prop="Rotation" />
      <el-table-column label="岸线类型" align="center" prop="ShorelineCategory" />
      <el-table-column label="信号标志" align="center" prop="Signaling" />
      <el-table-column label="信号标志" align="center" prop="Signalling" />
      <el-table-column label="运输可能描述" align="center" prop="TransportPossibilitiesDesc" />
      <el-table-column label="使用条件" align="center" prop="UsageConditions" />
      <el-table-column label="泊位宽度" align="center" prop="Width" />
      <el-table-column label="设施列表" align="center" prop="Facility" />
      <el-table-column label="船舶分类" align="center" prop="ShipCategories" />
      <el-table-column label="状态列表" align="center" prop="Status" />
      <el-table-column label="转运货物列表" align="center" prop="TransshipmentGoods" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['infrastructure:VNDSberth:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['infrastructure:VNDSberth:remove']">删除</el-button>
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

    <!-- 添加或修改VNDS-berth对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="VNDSberthRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="最大船舶长度" prop="MaximalShipLength">
          <el-input v-model="form.MaximalShipLength" placeholder="请输入最大船舶长度" />
        </el-form-item>
        <el-form-item label="最大船队高度" prop="MaximumConvoyHeight">
          <el-input v-model="form.MaximumConvoyHeight" placeholder="请输入最大船队高度" />
        </el-form-item>
        <el-form-item label="最大船队长度" prop="MaximumConvoyLength">
          <el-input v-model="form.MaximumConvoyLength" placeholder="请输入最大船队长度" />
        </el-form-item>
        <el-form-item label="最大船队宽度" prop="MaximumConvoyWidth">
          <el-input v-model="form.MaximumConvoyWidth" placeholder="请输入最大船队宽度" />
        </el-form-item>
        <el-form-item label="最大吃水深度" prop="MaximumDraught">
          <el-input v-model="form.MaximumDraught" placeholder="请输入最大吃水深度" />
        </el-form-item>
        <el-form-item label="最大船舶高度" prop="MaximumShipHeight">
          <el-input v-model="form.MaximumShipHeight" placeholder="请输入最大船舶高度" />
        </el-form-item>
        <el-form-item label="最大船舶宽度" prop="MaximumShipWidth">
          <el-input v-model="form.MaximumShipWidth" placeholder="请输入最大船舶宽度" />
        </el-form-item>
        <el-form-item label="最大停泊时间" prop="MaximumStayDurationH">
          <el-input v-model="form.MaximumStayDurationH" placeholder="请输入最大停泊时间" />
        </el-form-item>
        <el-form-item label="最小船舶长度" prop="MinimalShipLength">
          <el-input v-model="form.MinimalShipLength" placeholder="请输入最小船舶长度" />
        </el-form-item>
        <el-form-item label="系泊设施描述" prop="MooringFacilitiesDesc">
          <el-input v-model="form.MooringFacilitiesDesc" placeholder="请输入系泊设施描述" />
        </el-form-item>
        <el-form-item label="系泊设施数量" prop="NumberOfMooringFacilities">
          <el-input v-model="form.NumberOfMooringFacilities" placeholder="请输入系泊设施数量" />
        </el-form-item>
        <el-form-item label="泊位排数" prop="NumberOfRows">
          <el-input v-model="form.NumberOfRows" placeholder="请输入泊位排数" />
        </el-form-item>
        <el-form-item label="港口区域ID" prop="PortAreaId">
          <el-input v-model="form.PortAreaId" placeholder="请输入港口区域ID" />
        </el-form-item>
        <el-form-item label="码头质量" prop="QuayQuality">
          <el-input v-model="form.QuayQuality" placeholder="请输入码头质量" />
        </el-form-item>
        <el-form-item label="旋转角度" prop="Rotation">
          <el-input v-model="form.Rotation" placeholder="请输入旋转角度" />
        </el-form-item>
        <el-form-item label="岸线类型" prop="ShorelineCategory">
          <el-input v-model="form.ShorelineCategory" placeholder="请输入岸线类型" />
        </el-form-item>
        <el-form-item label="信号标志" prop="Signaling">
          <el-input v-model="form.Signaling" placeholder="请输入信号标志" />
        </el-form-item>
        <el-form-item label="信号标志" prop="Signalling">
          <el-input v-model="form.Signalling" placeholder="请输入信号标志" />
        </el-form-item>
        <el-form-item label="运输可能描述" prop="TransportPossibilitiesDesc">
          <el-input v-model="form.TransportPossibilitiesDesc" placeholder="请输入运输可能描述" />
        </el-form-item>
        <el-form-item label="使用条件" prop="UsageConditions">
          <el-input v-model="form.UsageConditions" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="泊位宽度" prop="Width">
          <el-input v-model="form.Width" placeholder="请输入泊位宽度" />
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

<script setup name="VNDSberth">
import { listVNDSberth, getVNDSberth, delVNDSberth, addVNDSberth, updateVNDSberth } from "@/api/infrastructure/VNDSberth"

const { proxy } = getCurrentInstance()

const VNDSberthList = ref([])
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
    GeoType: null,
    GeoGeneration: null,
    GeoJSON: null,
    Name: null,
    Geometry: null,
    VinCode: null,
    UnLocationCode: null,
    Note: null,
    RouteId: null,
    RouteKmBegin: null,
    RouteKmEnd: null,
    City: null,
    AdministrationId: null,
    FairwaySectionId: null,
    FairwayId: null,
    IsrsId: null,
    Condition: null,
    Category: null,
    WidthPosition: null,
    HasCrane: null,
    TerminalId: null,
    Address: null,
    Community: null,
    Accessibility: null,
    AvailableHeight: null,
    AvailableLength: null,
    AvailableWidth: null,
    ConstructionMaterial: null,
    ForeignCode: null,
    Length: null,
    MaximalShipLength: null,
    MaximumConvoyHeight: null,
    MaximumConvoyLength: null,
    MaximumConvoyWidth: null,
    MaximumDraught: null,
    MaximumShipHeight: null,
    MaximumShipWidth: null,
    MaximumStayDurationH: null,
    MinimalShipLength: null,
    MooringFacilitiesDesc: null,
    NumberOfMooringFacilities: null,
    NumberOfRows: null,
    PortAreaId: null,
    QuayQuality: null,
    Rotation: null,
    ShorelineCategory: null,
    Signaling: null,
    Signalling: null,
    TransportPossibilitiesDesc: null,
    UsageConditions: null,
    Width: null,
    Facility: null,
    ShipCategories: null,
    Status: null,
    TransshipmentGoods: null
  },
  rules: {
  }
})

const { queryParams, form, rules } = toRefs(data)

/** 查询VNDS-berth列表 */
function getList() {
  loading.value = true
  listVNDSberth(queryParams.value).then(response => {
    VNDSberthList.value = response.rows
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
    GeoType: null,
    GeoGeneration: null,
    GeoJSON: null,
    Name: null,
    Geometry: null,
    VinCode: null,
    UnLocationCode: null,
    Note: null,
    RouteId: null,
    RouteKmBegin: null,
    RouteKmEnd: null,
    City: null,
    AdministrationId: null,
    FairwaySectionId: null,
    FairwayId: null,
    IsrsId: null,
    Condition: null,
    Category: null,
    WidthPosition: null,
    HasCrane: null,
    TerminalId: null,
    Address: null,
    Community: null,
    Accessibility: null,
    AvailableHeight: null,
    AvailableLength: null,
    AvailableWidth: null,
    ConstructionMaterial: null,
    ForeignCode: null,
    Length: null,
    MaximalShipLength: null,
    MaximumConvoyHeight: null,
    MaximumConvoyLength: null,
    MaximumConvoyWidth: null,
    MaximumDraught: null,
    MaximumShipHeight: null,
    MaximumShipWidth: null,
    MaximumStayDurationH: null,
    MinimalShipLength: null,
    MooringFacilitiesDesc: null,
    NumberOfMooringFacilities: null,
    NumberOfRows: null,
    PortAreaId: null,
    QuayQuality: null,
    Rotation: null,
    ShorelineCategory: null,
    Signaling: null,
    Signalling: null,
    TransportPossibilitiesDesc: null,
    UsageConditions: null,
    Width: null,
    Facility: null,
    ShipCategories: null,
    Status: null,
    TransshipmentGoods: null
  }
  proxy.resetForm("VNDSberthRef")
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
  title.value = "添加VNDS-berth"
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset()
  const _Id = row.Id || ids.value
  getVNDSberth(_Id).then(response => {
    form.value = response.data
    open.value = true
    title.value = "修改VNDS-berth"
  })
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["VNDSberthRef"].validate(valid => {
    if (valid) {
      if (form.value.Id != null) {
        updateVNDSberth(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功")
          open.value = false
          getList()
        })
      } else {
        addVNDSberth(form.value).then(response => {
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
  proxy.$modal.confirm('是否确认删除VNDS-berth编号为"' + _Ids + '"的数据项？').then(function() {
    return delVNDSberth(_Ids)
  }).then(() => {
    getList()
    proxy.$modal.msgSuccess("删除成功")
  }).catch(() => {})
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('infrastructure/VNDSberth/export', {
    ...queryParams.value
  }, `VNDSberth_${new Date().getTime()}.xlsx`)
}

getList()
</script>
