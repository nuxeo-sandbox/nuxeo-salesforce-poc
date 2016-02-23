(function(){

    var client = new nuxeo.Client();

    var timeref = Date.now();

    var urlParams;
    (window.onpopstate = function () {
        var match,
            pl     = /\+/g,  // Regex for replacing addition symbol with a space
            search = /([^&=]+)=?([^&]*)/g,
            decode = function (s) { return decodeURIComponent(s.replace(pl, " ")); },
            query  = window.location.search.substring(1);

        urlParams = {};
        while (match = search.exec(query))
           urlParams[decode(match[1])] = decode(match[2]);
    })();

    document.addEventListener('pagechange', function(e) {
      if (e.pageNumber !== e.previousPageNumber) {

        console.log('page changed from ' + e.previousPageNumber + ' to ' + e.pageNumber);

        var timecurrent = Date.now();
        var diff = timecurrent - timeref;
        timeref = timecurrent;

        if ((diff)<1000) {
            return;
        } else {
            diff= Math.floor(diff /1000);
        }

        client.operation('ExtendedAuditLogOp')
          .params({
            event: 'pageChange',
            category: 'Document Tracking',
            pageNumber: e.previousPageNumber,
            timeSpentSeconds: diff,
            comment: "Watched page "+e.previousPageNumber+" for "+diff+" seconds"
          })
          .input(urlParams.uuid)
          .execute(function(error, folder) {
            if (error) {
              // something went wrong
              throw error;
            }

            console.log('Event Logged ');
          });
      }
    });

    $(document).ready(function() {

      var hidden, visibilityState, visibilityChange;

      if (typeof document.hidden !== "undefined") {
        hidden = "hidden", visibilityChange = "visibilitychange", visibilityState = "visibilityState";
      } else if (typeof document.msHidden !== "undefined") {
        hidden = "msHidden", visibilityChange = "msvisibilitychange", visibilityState = "msVisibilityState";
      }

      var document_hidden = document[hidden];

      document.addEventListener(visibilityChange, function() {
        if(document_hidden != document[hidden]) {
          if(document[hidden]) {
            console.log('Tab is hidden ');
          } else {
            console.log('Tab is displayed ');
          }
          document_hidden = document[hidden];
        }
      });
    });

})();