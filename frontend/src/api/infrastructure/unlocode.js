import request from '@/utils/request'

// 查询UNLOCODE列表
export function listUnlocode(query) {
  return request({
    url: '/infrastructure/unlocode/list',
    method: 'get',
    params: query
  })
}

// 查询UNLOCODE详细
export function getUnlocode(locode) {
  return request({
    url: '/infrastructure/unlocode/' + locode,
    method: 'get'
  })
}

// 新增UNLOCODE
export function addUnlocode(data) {
  return request({
    url: '/infrastructure/unlocode',
    method: 'post',
    data: data
  })
}

// 修改UNLOCODE
export function updateUnlocode(data) {
  return request({
    url: '/infrastructure/unlocode',
    method: 'put',
    data: data
  })
}

// 删除UNLOCODE
export function delUnlocode(locode) {
  return request({
    url: '/infrastructure/unlocode/' + locode,
    method: 'delete'
  })
}
