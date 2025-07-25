import request from '@/utils/request'

// 查询物流发布列表
export function listPublish(query) {
  return request({
    url: '/transport/publish/list',
    method: 'get',
    params: query
  })
}

// 查询物流发布详细
export function getPublish(id) {
  return request({
    url: '/transport/publish/' + id,
    method: 'get'
  })
}

// 新增物流发布
export function addPublish(data) {
  return request({
    url: '/transport/publish',
    method: 'post',
    data: data
  })
}

// 修改物流发布
export function updatePublish(data) {
  return request({
    url: '/transport/publish',
    method: 'put',
    data: data
  })
}

// 删除物流发布
export function delPublish(id) {
  return request({
    url: '/transport/publish/' + id,
    method: 'delete'
  })
}

