// ==================== API 调用封装 ====================

const API_BASE_URL = 'http://localhost:8080/webapi/positions';

// 通用请求函数
async function request(url, options = {}) {
    const defaultOptions = {
        headers: {
            'Content-Type': 'application/json',
        }
    };
    const mergedOptions = { ...defaultOptions, ...options };
    
    try {
        const response = await fetch(url, mergedOptions);
        const result = await response.json();
        
        if (result.code === 200) {
            return result;
        } else {
            showToast(result.message || '请求失败', 'error');
            throw new Error(result.message);
        }
    } catch (error) {
        if (error.name === 'TypeError') {
            showToast('网络连接失败，请检查后端服务', 'error');
        } else if (!error.message?.startsWith('请求失败')) {
            showToast(error.message || '服务器异常', 'error');
        }
        throw error;
    }
}

// 获取岗位列表（支持关键词和状态筛选）
async function fetchPositions(keyword = '', status = '') {
    const queryString = buildQueryParams(keyword, status);
    const result = await request(`${API_BASE_URL}${queryString}`);
    return result.data || [];
}

// 获取单个岗位
async function fetchPositionById(id) {
    const result = await request(`${API_BASE_URL}/${id}`);
    return result.data;
}

// 新增岗位
async function createPosition(positionData) {
    return await request(API_BASE_URL, {
        method: 'POST',
        body: JSON.stringify(positionData)
    });
}

// 更新岗位
async function updatePosition(id, positionData) {
    return await request(`${API_BASE_URL}/${id}`, {
        method: 'PUT',
        body: JSON.stringify(positionData)
    });
}

// 删除岗位
async function deletePosition(id) {
    return await request(`${API_BASE_URL}/${id}`, {
        method: 'DELETE'
    });
}

// 提交审批 (DRAFT -> PENDING)
async function submitApproval(id) {
    return await request(`${API_BASE_URL}/${id}/submit`, {
        method: 'POST'
    });
}

// 审批通过/驳回
async function approvePosition(id, action, comment = '') {
    return await request(`${API_BASE_URL}/${id}/approve`, {
        method: 'POST',
        body: JSON.stringify({ action, comment })
    });
}

// 关闭岗位 (PUBLISHED -> CLOSED)
async function closePosition(id) {
    return await request(`${API_BASE_URL}/${id}/close`, {
        method: 'POST'
    });
}

// Excel批量上传
async function uploadExcel(file) {
    const formData = new FormData();
    formData.append('file', file);
    
    try {
        const response = await fetch(API_BASE_URL + '/upload', {
            method: 'POST',
            body: formData
        });
        const result = await response.json();
        
        if (result.code === 200) {
            return result.data;
        } else {
            showToast(result.message || '上传失败', 'error');
            throw new Error(result.message);
        }
    } catch (error) {
        if (error.name === 'TypeError') {
            showToast('网络连接失败，请检查后端服务', 'error');
        } else {
            showToast(error.message || '上传失败', 'error');
        }
        throw error;
    }
}