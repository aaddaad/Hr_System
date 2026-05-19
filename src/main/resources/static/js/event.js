// ==================== 事件绑定 ====================

// 刷新列表的防抖版本
const debouncedRefresh = debounce(() => refreshList(), 300);

// 处理搜索输入
function handleSearchInput() {
    AppState.keyword = document.getElementById('searchInput').value;
    debouncedRefresh();
}

// 处理状态筛选变化
function handleStatusFilterChange() {
    AppState.statusFilter = document.getElementById('statusFilter').value;
    refreshList();
}

// 重置筛选
function handleReset() {
    AppState.keyword = '';
    AppState.statusFilter = '';
    document.getElementById('searchInput').value = '';
    document.getElementById('statusFilter').value = '';
    refreshList();
}

// 处理表单提交（新增/编辑）
async function handleFormSubmit(event) {
    event.preventDefault();
    
    const title = document.getElementById('formTitleInput').value.trim();
    const description = document.getElementById('formDescription').value;
    const status = document.getElementById('formStatus').value;
    
    // 前端校验
    if (!title) {
        document.getElementById('titleError').textContent = '岗位名称不能为空';
        return;
    }
    if (title.length > 100) {
        document.getElementById('titleError').textContent = '岗位名称长度不能超过100字符';
        return;
    }
    document.getElementById('titleError').textContent = '';
    
    if (AppState.submitting) return;
    AppState.submitting = true;
    document.getElementById('submitBtn').disabled = true;
    
    try {
        if (AppState.editId === null) {
            // 新增
            await createPosition({ title, description, status });
            showToast('新增成功', 'success');
        } else {
            // 编辑
            await updatePosition(AppState.editId, { title, description, status });
            showToast('修改成功', 'success');
        }
        
        // 重置表单并刷新
        resetFormState();
        await refreshList();
    } catch (error) {
        // 错误已在api中处理
    } finally {
        AppState.submitting = false;
        document.getElementById('submitBtn').disabled = false;
    }
}

// 取消编辑
function handleCancelEdit() {
    resetFormState();
}

// 处理表格操作按钮点击
async function handleTableAction(event) {
    const target = event.target;
    if (!target.matches('[data-action]')) return;
    
    const action = target.getAttribute('data-action');
    const id = parseInt(target.getAttribute('data-id'));
    
    switch (action) {
        case 'edit':
            try {
                const position = await fetchPositionById(id);
                setEditMode(position);
                // 滚动到表单区域
                document.querySelector('.form-section').scrollIntoView({ behavior: 'smooth' });
            } catch (error) {
                // 错误已处理
            }
            break;
            
        case 'submit':
            // 提交审批 DRAFT -> PENDING
            if (confirm('确定要提交该岗位进行审批吗？')) {
                await submitApproval(id);
                showToast('已提交审批', 'success');
                await refreshList();
            }
            break;
            
        case 'approve':
            // 审批通过
            if (confirm('确定通过该岗位的审批吗？通过后岗位将发布。')) {
                await approvePosition(id, 'APPROVE');
                showToast('审批通过，岗位已发布', 'success');
                await refreshList();
            }
            break;
            
        case 'reject':
            // 审批驳回，弹窗输入原因
            pendingRejectCallback = async (comment) => {
                await approvePosition(id, 'REJECT', comment);
                showToast('已驳回', 'warning');
                await refreshList();
            };
            document.getElementById('rejectModal').classList.add('show');
            document.getElementById('rejectComment').value = '';
            break;
            
        case 'close':
            // 关闭岗位
            if (confirm('确定要关闭该岗位吗？关闭后外部将不可见。')) {
                await closePosition(id);
                showToast('岗位已关闭', 'warning');
                await refreshList();
            }
            break;
            
        case 'reopen':
            // 重新发布（CLOSED -> PENDING）
            if (confirm('确定要重新发布该岗位吗？需要重新审批。')) {
                await submitApproval(id);
                showToast('已提交重新发布审批', 'success');
                await refreshList();
            }
            break;
            
        case 'delete':
            // 删除
            if (confirm('确定要删除该岗位吗？此操作不可恢复！')) {
                await deletePosition(id);
                showToast('删除成功', 'success');
                await refreshList();
            }
            break;
    }
}

// 文件上传相关
function handleFileSelect() {
    const fileInput = document.getElementById('uploadFile');
    const file = fileInput.files[0];
    if (file) {
        AppState.selectedFile = file;
        document.getElementById('uploadSubmitBtn').disabled = false;
        showToast(`已选择: ${file.name}`, 'success');
    } else {
        AppState.selectedFile = null;
        document.getElementById('uploadSubmitBtn').disabled = true;
    }
}

async function handleUpload() {
    if (!AppState.selectedFile) {
        showToast('请先选择文件', 'warning');
        return;
    }
    
    if (AppState.uploading) return;
    AppState.uploading = true;
    const uploadBtn = document.getElementById('uploadSubmitBtn');
    const originalText = uploadBtn.textContent;
    uploadBtn.textContent = '⏳ 上传中...';
    uploadBtn.disabled = true;
    
    try {
        const result = await uploadExcel(AppState.selectedFile);
        showUploadResult(result);
        await refreshList();
        // 清空文件选择
        document.getElementById('uploadFile').value = '';
        AppState.selectedFile = null;
        uploadBtn.disabled = true;
        uploadBtn.textContent = originalText;
    } catch (error) {
        uploadBtn.textContent = originalText;
        uploadBtn.disabled = false;
    } finally {
        AppState.uploading = false;
    }
}

function triggerFileSelect() {
    document.getElementById('uploadFile').click();
}

// 模态框事件
function closeModal() {
    document.getElementById('rejectModal').classList.remove('show');
    pendingRejectCallback = null;
}

async function confirmReject() {
    const comment = document.getElementById('rejectComment').value.trim();
    if (!comment) {
        showToast('请填写驳回原因', 'warning');
        return;
    }
    if (pendingRejectCallback) {
        await pendingRejectCallback(comment);
    }
    closeModal();
}

// 初始化所有事件监听
function initEventListeners() {
    // 搜索与筛选
    const searchInput = document.getElementById('searchInput');
    searchInput.addEventListener('input', handleSearchInput);
    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') handleSearchInput();
    });
    document.getElementById('searchBtn').addEventListener('click', () => refreshList());
    document.getElementById('statusFilter').addEventListener('change', handleStatusFilterChange);
    document.getElementById('resetBtn').addEventListener('click', handleReset);
    
    // 表单
    document.getElementById('positionForm').addEventListener('submit', handleFormSubmit);
    document.getElementById('cancelEditBtn').addEventListener('click', handleCancelEdit);
    
    // 表格操作（事件委托）
    document.getElementById('tableBody').addEventListener('click', handleTableAction);
    
    // 文件上传
    document.getElementById('uploadBtn').addEventListener('click', triggerFileSelect);
    document.getElementById('uploadSubmitBtn').addEventListener('click', handleUpload);
    document.getElementById('uploadFile').addEventListener('change', handleFileSelect);
    
    // 模态框
    document.getElementById('rejectConfirmBtn').addEventListener('click', confirmReject);
    document.getElementById('rejectCancelBtn').addEventListener('click', closeModal);
    document.getElementById('rejectModal').addEventListener('click', (e) => {
        if (e.target === document.getElementById('rejectModal')) closeModal();
    });
}