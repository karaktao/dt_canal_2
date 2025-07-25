import request from '@/utils/request'

// 查询weatherMinutely列表
export function listMinutely(query) {
  return request({
    url: '/weather/Minutely/list',
    method: 'get',
    params: query
  })
}

// 查询weatherMinutely详细
export function getMinutely(id) {
  return request({
    url: '/weather/Minutely/' + id,
    method: 'get'
  })
}

// 新增weatherMinutely
export function addMinutely(data) {
  return request({
    url: '/weather/Minutely',
    method: 'post',
    data: data
  })
}

// 修改weatherMinutely
export function updateMinutely(data) {
  return request({
    url: '/weather/Minutely',
    method: 'put',
    data: data
  })
}

// 删除weatherMinutely
export function delMinutely(id) {
  return request({
    url: '/weather/Minutely/' + id,
    method: 'delete'
  })
}
