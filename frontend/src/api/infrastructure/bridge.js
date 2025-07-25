import request from '@/utils/request'

// 查询bridgeinfo列表
export function listBridge(query) {
  return request({
    url: '/infrastructure/bridge/list',
    method: 'get',
    params: query
  })
}

// 查询bridgeinfo详细
export function getBridge(id) {
  return request({
    url: '/infrastructure/bridge/' + id,
    method: 'get'
  })
}

// 新增bridgeinfo
export function addBridge(data) {
  return request({
    url: '/infrastructure/bridge',
    method: 'post',
    data: data
  })
}

// 修改bridgeinfo
export function updateBridge(data) {
  return request({
    url: '/infrastructure/bridge',
    method: 'put',
    data: data
  })
}

// 删除bridgeinfo
export function delBridge(id) {
  return request({
    url: '/infrastructure/bridge/' + id,
    method: 'delete'
  })
}
