// lib/logstash/filters/example.rb
require 'logstash/filters/base'

class LogStash::Filters::Example < LogStash::Filters::Base
  config_name 'example'

  def filter(event)
    return unless filter?(event)

    event.set('message', event.get('message').downcase)

    filter_matched(event)
  end
end
