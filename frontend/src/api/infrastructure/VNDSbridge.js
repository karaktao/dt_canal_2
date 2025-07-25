import request from '@/utils/request'

// 查询VNDSbridge列表
export function listVNDSbridge(query) {
  return request({
    url: '/infrastructure/VNDSbridge/list',
    method: 'get',
    params: query
  })
}

// 查询VNDSbridge详细
export function getVNDSbridge(Id) {
  return request({
    url: '/infrastructure/VNDSbridge/' + Id,
    method: 'get'
  })
}

// 新增VNDSbridge
export function addVNDSbridge(data) {
  return request({
    url: '/infrastructure/VNDSbridge',
    method: 'post',
    data: data
  })
}

// 修改VNDSbridge
export function updateVNDSbridge(data) {
  return request({
    url: '/infrastructure/VNDSbridge',
    method: 'put',
    data: data
  })
}

// 删除VNDSbridge
export function delVNDSbridge(Id) {
  return request({
    url: '/infrastructure/VNDSbridge/' + Id,
    method: 'delete'
  })
}
