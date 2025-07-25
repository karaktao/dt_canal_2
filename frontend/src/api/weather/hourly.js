import request from '@/utils/request'

// 查询weatherHourly列表
export function listHourly(query) {
  return request({
    url: '/weather/hourly/list',
    method: 'get',
    params: query
  })
}

// 查询weatherHourly详细
export function getHourly(id) {
  return request({
    url: '/weather/hourly/' + id,
    method: 'get'
  })
}

// 新增weatherHourly
export function addHourly(data) {
  return request({
    url: '/weather/hourly',
    method: 'post',
    data: data
  })
}

// 修改weatherHourly
export function updateHourly(data) {
  return request({
    url: '/weather/hourly',
    method: 'put',
    data: data
  })
}

// 删除weatherHourly
export function delHourly(id) {
  return request({
    url: '/weather/hourly/' + id,
    method: 'delete'
  })
}
