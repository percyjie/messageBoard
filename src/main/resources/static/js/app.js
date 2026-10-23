(function () {
    'use strict';

    const relativeFormatter = new Intl.RelativeTimeFormat('zh-CN', {numeric: 'auto'});

    function relativeTime(date) {
        const diff = date.getTime() - Date.now();
        const abs = Math.abs(diff);
        if (abs < 60 * 1000) {
            return '刚刚';
        }
        if (abs < 60 * 60 * 1000) {
            return relativeFormatter.format(Math.round(diff / 60000), 'minute');
        }
        if (abs < 24 * 60 * 60 * 1000) {
            return relativeFormatter.format(Math.round(diff / 3600000), 'hour');
        }
        if (abs < 7 * 24 * 60 * 60 * 1000) {
            return relativeFormatter.format(Math.round(diff / 86400000), 'day');
        }
        return null;
    }

    document.querySelectorAll('[data-relative-time]').forEach(function (element) {
        const iso = element.getAttribute('datetime');
        if (!iso) {
            return;
        }
        const date = new Date(iso);
        if (Number.isNaN(date.getTime())) {
            return;
        }
        const text = relativeTime(date);
        if (text) {
            element.textContent = text;
        }
    });

    const textarea = document.getElementById('messageContent');
    const counter = document.getElementById('charCount');
    if (textarea && counter) {
        const updateCounter = function () {
            counter.textContent = textarea.value.length;
        };
        textarea.addEventListener('input', updateCounter);
        updateCounter();
    }

    const fileInput = document.getElementById('avatarFile');
    const preview = document.getElementById('avatarPreview');
    if (fileInput && preview) {
        fileInput.addEventListener('change', function () {
            const file = fileInput.files && fileInput.files[0];
            if (!file || !file.type.startsWith('image/')) {
                return;
            }
            const objectUrl = URL.createObjectURL(file);
            preview.src = objectUrl;
            preview.hidden = false;
            preview.onload = function () {
                URL.revokeObjectURL(objectUrl);
            };
        });
    }

    const modalElement = document.getElementById('confirmModal');
    if (modalElement && window.bootstrap) {
        const modal = new window.bootstrap.Modal(modalElement);
        const textElement = modalElement.querySelector('[data-confirm-text]');
        const confirmButton = modalElement.querySelector('[data-confirm-ok]');
        let pendingForm = null;

        document.querySelectorAll('form[data-confirm]').forEach(function (form) {
            form.addEventListener('submit', function (event) {
                if (form.dataset.confirmed === 'true') {
                    return;
                }
                event.preventDefault();
                pendingForm = form;
                textElement.textContent = form.dataset.confirm || '确定要执行此操作吗？';
                modal.show();
            });
        });

        confirmButton.addEventListener('click', function () {
            if (!pendingForm) {
                return;
            }
            pendingForm.dataset.confirmed = 'true';
            modal.hide();
            pendingForm.submit();
        });
    }
})();
