document.addEventListener('DOMContentLoaded', () => {
    const user = requireAuth();
    if (!user) return;

    // Greeting
    const hour = new Date().getHours();
    const greeting = hour < 12 ? 'Good morning' : hour < 17 ? 'Good afternoon' : 'Good evening';
    document.getElementById('greetingText').textContent = `${greeting}, ${user.firstName}.`;

    const apps       = getApplications().filter(a => a.userId === user.id);
    const interviews = getInterviews().filter(i => i.userId === user.id);
    const assessments= getAssessments().filter(a => a.userId === user.id);

    // Stats
    const upcoming = interviews.filter(i => {
        const h = hoursUntil(i.date, i.startTime);
        return h >= 0;
    });
    const dueSoon = assessments.filter(a => !a.completed && new Date(a.dueDate) >= new Date());

    document.getElementById('statTotal').textContent      = apps.length;
    document.getElementById('statInterviews').textContent = upcoming.length;
    document.getElementById('statOffers').textContent     = apps.filter(a => a.status === 'Offer').length;
    document.getElementById('statAssessments').textContent= dueSoon.length;

    // Recent applications (last 5 by dateApplied desc)
    const recent = [...apps]
        .sort((a, b) => new Date(b.dateApplied) - new Date(a.dateApplied))
        .slice(0, 5);

    const tbody = document.getElementById('recentAppsBody');
    if (recent.length === 0) {
        tbody.innerHTML = `<tr><td colspan="4" class="empty-state" style="padding:32px;text-align:center;color:#94a3b8;font-size:13px;">No applications yet. Click + to add one.</td></tr>`;
    } else {
        tbody.innerHTML = recent.map(a => `
            <tr>
                <td><span style="font-weight:500;">${esc(a.jobTitle)}</span></td>
                <td class="td-muted">${esc(a.company)}</td>
                <td><span class="status-badge status-${a.status.toLowerCase().replace(' ','-')}">${esc(a.status)}</span></td>
                <td class="td-muted">${formatDate(a.dateApplied)}</td>
            </tr>
        `).join('');
    }

    // Alerts
    renderAlerts(user.id);
});

function renderAlerts(userId) {
    const alerts = getUpcomingAlerts(userId);
    const list   = document.getElementById('alertsList');
    const count  = document.getElementById('alertCount');

    if (alerts.length === 0) {
        list.innerHTML = `<div class="no-alerts">No upcoming deadlines in the next 48 hours.</div>`;
        count.style.display = 'none';
        return;
    }

    count.textContent = alerts.length;
    count.style.display = 'inline-flex';

    list.innerHTML = alerts.map(a => {
        const h = Math.round(a.hours);
        const timeLabel = h < 1 ? 'Soon' : h < 24 ? `${h}h` : `${Math.round(h/24)}d`;

        if (a.type === 'interview') {
            const iv = a.item;
            return `
                <div class="alert-item alert-interview">
                    <div class="alert-icon">
                        <svg viewBox="0 0 24 24"><rect x="3" y="4" width="18" height="18" rx="2"/><path d="M8 2v4m8-4v4M3 10h18"/></svg>
                    </div>
                    <div class="alert-content">
                        <strong>Interview: ${esc(iv.roleTitle)}</strong>
                        <span>${formatDate(iv.date)} at ${iv.startTime} · ${esc(iv.type)}</span>
                    </div>
                    <span class="alert-time">${timeLabel}</span>
                </div>`;
        } else {
            const as = a.item;
            return `
                <div class="alert-item alert-assessment">
                    <div class="alert-icon">
                        <svg viewBox="0 0 24 24"><path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2"/><rect x="9" y="3" width="6" height="4" rx="1"/></svg>
                    </div>
                    <div class="alert-content">
                        <strong>Assessment Due: ${esc(as.title)}</strong>
                        <span>Due ${formatDate(as.dueDate)}</span>
                    </div>
                    <span class="alert-time">${timeLabel}</span>
                </div>`;
        }
    }).join('');
}

function esc(str) {
    return String(str || '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}
