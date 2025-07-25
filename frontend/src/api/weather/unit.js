import request from '@/utils/request'

// 查询weatherUnit列表
export function listUnit(query) {
  return request({
    url: '/weather/unit/list',
    method: 'get',
    params: query
  })
}

// 查询weatherUnit详细
export function getUnit(id) {
  return request({
    url: '/weather/unit/' + id,
    method: 'get'
  })
}

// 新增weatherUnit
export function addUnit(data) {
  return request({
    url: '/weather/unit',
    method: 'post',
    data: data
  })
}

// 修改weatherUnit
export function updateUnit(data) {
  return request({
    url: '/weather/unit',
    method: 'put',
    data: data
  })
}

// 删除weatherUnit
export function delUnit(id) {
  return request({
    url: '/weather/unit/' + id,
    method: 'delete'
  })
}
