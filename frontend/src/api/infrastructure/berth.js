import request from '@/utils/request'

// 查询berthInfo列表
export function listBerth(query) {
  return request({
    url: '/infrastructure/berth/list',
    method: 'get',
    params: query
  })
}

// 查询berthInfo详细
export function getBerth(id) {
  return request({
    url: '/infrastructure/berth/' + id,
    method: 'get'
  })
}

// 新增berthInfo
export function addBerth(data) {
  return request({
    url: '/infrastructure/berth',
    method: 'post',
    data: data
  })
}

// 修改berthInfo
export function updateBerth(data) {
  return request({
    url: '/infrastructure/berth',
    method: 'put',
    data: data
  })
}

// 删除berthInfo
export function delBerth(id) {
  return request({
    url: '/infrastructure/berth/' + id,
    method: 'delete'
  })
}
