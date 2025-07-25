import request from '@/utils/request'

// 查询VNDS-berth列表
export function listVNDSberth(query) {
  return request({
    url: '/infrastructure/VNDSberth/list',
    method: 'get',
    params: query
  })
}

// 查询VNDS-berth详细
export function getVNDSberth(Id) {
  return request({
    url: '/infrastructure/VNDSberth/' + Id,
    method: 'get'
  })
}

// 新增VNDS-berth
export function addVNDSberth(data) {
  return request({
    url: '/infrastructure/VNDSberth',
    method: 'post',
    data: data
  })
}

// 修改VNDS-berth
export function updateVNDSberth(data) {
  return request({
    url: '/infrastructure/VNDSberth',
    method: 'put',
    data: data
  })
}

// 删除VNDS-berth
export function delVNDSberth(Id) {
  return request({
    url: '/infrastructure/VNDSberth/' + Id,
    method: 'delete'
  })
}
