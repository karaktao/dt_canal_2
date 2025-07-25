import request from '@/utils/request'

// 查询weatherCurrent列表
export function listCurrent(query) {
  return request({
    url: '/weather/current/list',
    method: 'get',
    params: query
  })
}

// 查询weatherCurrent详细
export function getCurrent(id) {
  return request({
    url: '/weather/current/' + id,
    method: 'get'
  })
}

// 新增weatherCurrent
export function addCurrent(data) {
  return request({
    url: '/weather/current',
    method: 'post',
    data: data
  })
}

// 修改weatherCurrent
export function updateCurrent(data) {
  return request({
    url: '/weather/current',
    method: 'put',
    data: data
  })
}

// 删除weatherCurrent
export function delCurrent(id) {
  return request({
    url: '/weather/current/' + id,
    method: 'delete'
  })
}
