// ==================== 工具函数 ====================

// Toast 提示
let toastTimer = null;
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    if (toastTimer) clearTimeout(toastTimer);
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    toastTimer = setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// 防抖函数
function debounce(fn, delay = 300) {
    let timer = null;
    return function(...args) {
        if (timer) clearTimeout(timer);
        timer = setTimeout(() => fn.apply(this, args), delay);
    };
}

// 构建查询参数
function buildQueryParams(keyword, status) {
    const params = [];
    if (keyword && keyword.trim()) {
        params.push(`keyword=${encodeURIComponent(keyword.trim())}`);
    }
    if (status && status.trim()) {
        params.push(`status=${encodeURIComponent(status)}`);
    }
    return params.length ? '?' + params.join('&') : '';
}

// 获取状态标签HTML
function getStatusBadgeHtml(status) {
    const statusMap = {
        'DRAFT': '📝 草稿',
        'PENDING': '⏳ 待审批',
        'PUBLISHED': '✅ 已发布',
        'CLOSED': '🔒 已关闭'
    };
    const displayText = statusMap[status] || status;
    return `<span class="status-badge status-${status}">${displayText}</span>`;
}

// 截断文本
function truncateText(text, maxLength = 60) {
    if (!text) return '-';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

// 转义HTML防止XSS
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// 获取状态对应的操作按钮（根据状态流转规则）
function getStatusActions(position, state) {
    const status = position.status;
    const actions = [];
    
    // 编辑按钮（所有状态都可编辑）
    actions.push(`<button class="btn btn-outline btn-sm" data-action="edit" data-id="${position.id}">✏️ 编辑</button>`);
    
    // 根据状态显示不同操作
    switch (status) {
        case 'DRAFT':
            actions.push(`<button class="btn btn-primary btn-sm" data-action="submit" data-id="${position.id}">📤 提交审批</button>`);
            break;
        case 'PENDING':
            actions.push(`<button class="btn btn-success btn-sm" data-action="approve" data-id="${position.id}">✅ 通过</button>`);
            actions.push(`<button class="btn btn-danger btn-sm" data-action="reject" data-id="${position.id}">❌ 驳回</button>`);
            break;
        case 'PUBLISHED':
            actions.push(`<button class="btn btn-warning btn-sm" data-action="close" data-id="${position.id}">🔒 关闭</button>`);
            break;
        case 'CLOSED':
            actions.push(`<button class="btn btn-primary btn-sm" data-action="reopen" data-id="${position.id}">🔄 重新发布</button>`);
            break;
    }
    
    // 删除按钮（已发布不可直接删除，但前端仍显示，后端会校验）
    const deleteClass = status === 'PUBLISHED' ? 'btn-outline' : 'btn-danger';
    actions.push(`<button class="btn ${deleteClass} btn-sm" data-action="delete" data-id="${position.id}">🗑️ 删除</button>`);
    
    return actions.join('');
}

// 获取当前用户角色（演示版固定为ADMIN）
function getCurrentRole() {
    return 'ADMIN';
}