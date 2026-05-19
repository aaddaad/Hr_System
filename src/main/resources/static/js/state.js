// ==================== 全局状态管理 ====================

const AppState = {
    positions: [],      // 岗位列表
    keyword: '',        // 搜索关键词
    statusFilter: '',   // 状态筛选
    editId: null,       // 正在编辑的ID，null表示新增模式
    loading: false,     // 加载中
    submitting: false,  // 提交中
    uploading: false,   // 上传中
    selectedFile: null  // 选中的上传文件
};

// 更新状态并触发重新渲染（可选）
let pendingRejectCallback = null;  // 审批驳回的回调

// 重置状态（不清空列表）
function resetFormState() {
    AppState.editId = null;
    document.getElementById('formTitle').textContent = '新增岗位';
    document.getElementById('submitBtn').textContent = '➕ 新增岗位';
    document.getElementById('cancelEditBtn').style.display = 'none';
    document.getElementById('formTitleInput').value = '';
    document.getElementById('formDescription').value = '';
    document.getElementById('formStatus').value = 'DRAFT';
    document.getElementById('titleError').textContent = '';
}

// 设置编辑模式
function setEditMode(position) {
    AppState.editId = position.id;
    document.getElementById('formTitle').textContent = '编辑岗位';
    document.getElementById('submitBtn').textContent = '💾 保存修改';
    document.getElementById('cancelEditBtn').style.display = 'inline-block';
    document.getElementById('formTitleInput').value = position.title;
    document.getElementById('formDescription').value = position.description || '';
    document.getElementById('formStatus').value = position.status;
    document.getElementById('titleError').textContent = '';
}