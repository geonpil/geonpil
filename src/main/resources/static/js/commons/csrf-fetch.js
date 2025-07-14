(function (global) {
  "use strict";

  /**
   * Read content of a <meta> tag by name.
   * @param {string} name meta name attribute
   * @returns {string|null}
   */
  function getMetaContent(name) {
    const el = document.querySelector(`meta[name='${name}']`);
    return el ? el.content : null;
  }

  /**
   * Wrapper around window.fetch that adds CSRF header pulled from meta tags created by Spring Security.
   * Falls back gracefully if token is missing.
   *
   * @param {RequestInfo} url
   * @param {RequestInit} [options]
   * @returns {Promise<Response>}
   */
  function csrfFetch(url, options = {}) {
    const token = getMetaContent('_csrf');
    const headerName = getMetaContent('_csrf_header') || 'X-CSRF-TOKEN';

    const baseOptions = {
      credentials: 'same-origin',
      headers: token ? { [headerName]: token } : {}
    };

    // Merge caller-provided options, giving precedence to caller headers if they override
    return fetch(url, {
      ...baseOptions,
      ...options,
      headers: { ...baseOptions.headers, ...(options.headers || {}) }
    });
  }

  // Expose globally
  global.csrfFetch = csrfFetch;
})(typeof window !== 'undefined' ? window : this); 