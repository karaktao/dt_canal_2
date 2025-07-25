import request from '@/utils/request'

// 查询MunicipalityReferenceTable列表
export function listMunicipalityReference(query) {
  return request({
    url: '/infrastructure/municipalityReference/list',
    method: 'get',
    params: query
  })
}

// 查询MunicipalityReferenceTable详细
export function getMunicipalityReference(municipalityCode) {
  return request({
    url: '/infrastructure/municipalityReference/' + municipalityCode,
    method: 'get'
  })
}

// 新增MunicipalityReferenceTable
export function addMunicipalityReference(data) {
  return request({
    url: '/infrastructure/municipalityReference',
    method: 'post',
    data: data
  })
}

// 修改MunicipalityReferenceTable
export function updateMunicipalityReference(data) {
  return request({
    url: '/infrastructure/municipalityReference',
    method: 'put',
    data: data
  })
}

// 删除MunicipalityReferenceTable
export function delMunicipalityReference(municipalityCode) {
  return request({
    url: '/infrastructure/municipalityReference/' + municipalityCode,
    method: 'delete'
  })
}
