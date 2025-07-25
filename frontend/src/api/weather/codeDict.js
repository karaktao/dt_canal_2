import request from '@/utils/request'

// 查询weatherCodeDict列表
export function listCodeDict(query) {
  return request({
    url: '/weather/codeDict/list',
    method: 'get',
    params: query
  })
}

// 查询weatherCodeDict详细
export function getCodeDict(code) {
  return request({
    url: '/weather/codeDict/' + code,
    method: 'get'
  })
}

// 新增weatherCodeDict
export function addCodeDict(data) {
  return request({
    url: '/weather/codeDict',
    method: 'post',
    data: data
  })
}

// 修改weatherCodeDict
export function updateCodeDict(data) {
  return request({
    url: '/weather/codeDict',
    method: 'put',
    data: data
  })
}

// 删除weatherCodeDict
export function delCodeDict(code) {
  return request({
    url: '/weather/codeDict/' + code,
    method: 'delete'
  })
}
