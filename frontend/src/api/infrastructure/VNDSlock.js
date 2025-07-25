import request from '@/utils/request'

// 查询VNDSlock列表
export function listVNDSlock(query) {
  return request({
    url: '/infrastructure/VNDSlock/list',
    method: 'get',
    params: query
  })
}

// 查询VNDSlock详细
export function getVNDSlock(Id) {
  return request({
    url: '/infrastructure/VNDSlock/' + Id,
    method: 'get'
  })
}

// 新增VNDSlock
export function addVNDSlock(data) {
  return request({
    url: '/infrastructure/VNDSlock',
    method: 'post',
    data: data
  })
}

// 修改VNDSlock
export function updateVNDSlock(data) {
  return request({
    url: '/infrastructure/VNDSlock',
    method: 'put',
    data: data
  })
}

// 删除VNDSlock
export function delVNDSlock(Id) {
  return request({
    url: '/infrastructure/VNDSlock/' + Id,
    method: 'delete'
  })
}
