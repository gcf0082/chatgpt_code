Gem::Specification.new do |s|
  s.name          = 'logstash-filter-example'
  s.version       = '0.1.0'
  s.description   = 'Logstash Filter Plugin Example'
  s.authors       = ['Your Name']
  s.email         = ['your.email@example.com']
  s.homepage      = 'https://github.com/your-username/logstash-filter-example'
  s.summary       = 'A Logstash Filter Plugin Example'

  s.files         = Dir['lib/**/*', 'spec/**/*', 'Gemfile', 'LICENSE', 'README.md']
  s.require_paths = ['lib']
  s.add_dependency 'logstash-core-plugin-api', '>= 1.60.0'
end
