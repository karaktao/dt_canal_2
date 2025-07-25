import request from '@/utils/request'

// 查询rwsCatalogus列表
export function listRwsCatalogus(query) {
  return request({
    url: '/infrastructure/rwsCatalogus/list',
    method: 'get',
    params: query
  })
}

// 查询rwsCatalogus详细
export function getRwsCatalogus(id) {
  return request({
    url: '/infrastructure/rwsCatalogus/' + id,
    method: 'get'
  })
}

// 新增rwsCatalogus
export function addRwsCatalogus(data) {
  return request({
    url: '/infrastructure/rwsCatalogus',
    method: 'post',
    data: data
  })
}

// 修改rwsCatalogus
export function updateRwsCatalogus(data) {
  return request({
    url: '/infrastructure/rwsCatalogus',
    method: 'put',
    data: data
  })
}

// 删除rwsCatalogus
export function delRwsCatalogus(id) {
  return request({
    url: '/infrastructure/rwsCatalogus/' + id,
    method: 'delete'
  })
}



