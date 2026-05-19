// ==================== 渲染函数 ====================

// 渲染岗位列表
function renderPositions() {
    const tbody = document.getElementById('tableBody');
    const recordCountSpan = document.getElementById('recordCount');
    
    if (AppState.loading) {
        tbody.innerHTML = '<tr class="empty-row"><td colspan="5">加载中...</td></tr>';
        recordCountSpan.textContent = '共 0 条记录';
        return;
    }
    
    if (!AppState.positions || AppState.positions.length === 0) {
        tbody.innerHTML = '<tr class="empty-row"><td colspan="5">暂无岗位数据 — 请通过表单新增或上传 Excel 导入</td></tr>';
        recordCountSpan.textContent = '共 0 条记录';
        return;
    }
    
    const rows = AppState.positions.map(pos => {
        const descPreview = truncateText(pos.description || '-', 50);
        const escapedDesc = escapeHtml(descPreview);
        const escapedTitle = escapeHtml(pos.title);
        
        return `
            <tr>
                <td>${pos.id}</td>
                <td><strong>${escapedTitle}</strong></td>
                <td title="${escapeHtml(pos.description || '')}">${escapedDesc}</td>
                <td>${getStatusBadgeHtml(pos.status)}</td>
                <td class="action-buttons">${getStatusActions(pos, AppState)}</td>
            </tr>
        `;
    }).join('');
    
    tbody.innerHTML = rows;
    recordCountSpan.textContent = `共 ${AppState.positions.length} 条记录`;
}

// 显示上传结果弹窗
function showUploadResult(result) {
    const { successCount, failCount, errors } = result;
    let message = `✅ 成功: ${successCount} 条`;
    if (failCount > 0) {
        message += `\n❌ 失败: ${failCount} 条`;
        if (errors && errors.length > 0) {
            message += `\n\n错误详情:\n${errors.slice(0, 5).join('\n')}`;
            if (errors.length > 5) {
                message += `\n... 共 ${errors.length} 条错误`;
            }
        }
    }
    showToast(`导入完成: 成功 ${successCount} 条, 失败 ${failCount} 条`, failCount > 0 ? 'warning' : 'success');
    // 如果有错误，在控制台输出详细
    if (errors && errors.length > 0) {
        console.error('Excel导入错误详情:', errors);
    }
}

// 刷新列表（保持当前搜索筛选条件）
async function refreshList() {
    AppState.loading = true;
    renderPositions();
    
    try {
        const positions = await fetchPositions(AppState.keyword, AppState.statusFilter);
        AppState.positions = positions;
    } catch (error) {
        AppState.positions = [];
    } finally {
        AppState.loading = false;
        renderPositions();
    }
}

// 加载列表（重置搜索筛选）
async function loadPositions() {
    AppState.keyword = '';
    AppState.statusFilter = '';
    document.getElementById('searchInput').value = '';
    document.getElementById('statusFilter').value = '';
    await refreshList();
}