var nuxeo = nuxeo || {};
nuxeo.Picker = nuxeo.Picker || {
  open: function (options, cb) {
    if (typeof(options) === "function") {
      cb = options;
      options = {};
    }
    var width = options.width || 650,
        height = options.height || 600,
        left = options.left || (window.screenX + window.outerWidth / 2 - width / 2),
        top = options.top || (window.screenY + window.outerHeight / 2 - height / 2),
        url = options.url || document.currentScript;
    var popup = window.open(url, 'popup', 'height=' + height + ',width=' + width + ',top=' + top + ',left=' + left);
    window.addEventListener('message', function (e) {
      popup.close();
      cb && cb(e.data);
    });
  }
};
