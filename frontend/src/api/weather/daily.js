import request from '@/utils/request'

// 查询weatherDaily列表
export function listDaily(query) {
  return request({
    url: '/weather/daily/list',
    method: 'get',
    params: query
  })
}

// 查询weatherDaily详细
export function getDaily(id) {
  return request({
    url: '/weather/daily/' + id,
    method: 'get'
  })
}

// 新增weatherDaily
export function addDaily(data) {
  return request({
    url: '/weather/daily',
    method: 'post',
    data: data
  })
}

// 修改weatherDaily
export function updateDaily(data) {
  return request({
    url: '/weather/daily',
    method: 'put',
    data: data
  })
}

// 删除weatherDaily
export function delDaily(id) {
  return request({
    url: '/weather/daily/' + id,
    method: 'delete'
  })
}
