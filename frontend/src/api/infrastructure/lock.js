import request from '@/utils/request'

// 查询lockinfo列表
export function listLock(query) {
  return request({
    url: '/infrastructure/lock/list',
    method: 'get',
    params: query
  })
}

// 查询lockinfo详细
export function getLock(id) {
  return request({
    url: '/infrastructure/lock/' + id,
    method: 'get'
  })
}

// 新增lockinfo
export function addLock(data) {
  return request({
    url: '/infrastructure/lock',
    method: 'post',
    data: data
  })
}

// 修改lockinfo
export function updateLock(data) {
  return request({
    url: '/infrastructure/lock',
    method: 'put',
    data: data
  })
}

// 删除lockinfo
export function delLock(id) {
  return request({
    url: '/infrastructure/lock/' + id,
    method: 'delete'
  })
}
