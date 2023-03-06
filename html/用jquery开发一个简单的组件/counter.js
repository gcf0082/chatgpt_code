(function($) {
  $.fn.counter = function(options) {
    var defaults = {
      start: 0,
      step: 1,
      delay: 1000
    };
    var settings = $.extend({}, defaults, options);
    
    return this.each(function() {
      var $this = $(this);
      var count = settings.start;
      
      var intervalId = setInterval(function() {
        count += settings.step;
        $this.text(count);
      }, settings.delay);
      
      $this.data("intervalId", intervalId);
    });
  };
})(jQuery);
