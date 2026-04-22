async function authFetch(url, options = {}) {
    const response = await fetch(url, options);

    if (response.status === 401) {
        window.location.href = "/experiencias/login.html";
        return null;
    }

    if (response.status === 403) {
        window.location.href = "/";
        return null;
    }

    return response;
}

async function logout() {
    await fetch("/api/auth/logout", { method: "POST" });
    window.location.href = "/experiencias/login.html";
}
