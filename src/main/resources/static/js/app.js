async function loadGroups() {
    const res = await fetch('/api/groups');
    if (!res.ok) {
        console.error('Failed to fetch groups', res.status);
        return;
    }
    const groups = await res.json();
    const list = document.getElementById('groupsList');
    const empty = document.getElementById('groupsEmpty');
    list.innerHTML = '';
    if (!groups || groups.length === 0) {
        empty.style.display = 'flex';
        return;
    }
    empty.style.display = 'none';
    groups.forEach(g => {
        const li = document.createElement('li');
        li.innerHTML = `<div class="g-name">${escapeHtml(g.name)}</div>` + (g.description ? `<div class="g-desc">${escapeHtml(g.description)}</div>` : '');
        list.appendChild(li);
    });
}

function escapeHtml(s) {
    const div = document.createElement('div');
    div.textContent = s || '';
    return div.innerHTML;
}

document.addEventListener('DOMContentLoaded', () => {
    loadGroups();
    
    const form = document.getElementById('createForm');
    const refreshBtn = document.getElementById('refreshBtn');
    
    if (refreshBtn) {
        refreshBtn.addEventListener('click', loadGroups);
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const name = document.getElementById('name').value.trim();
        const description = document.getElementById('description').value.trim();
        if (!name) {
            alert('Name is required');
            return;
        }
        const res = await fetch('/api/groups', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, description })
        });
        if (res.ok) {
            document.getElementById('name').value = '';
            document.getElementById('description').value = '';
            loadGroups();
        } else {
            const text = await res.text();
            alert('Failed to create group: ' + res.status + ' ' + text);
        }
    });
});
