require 'imatcher'

file1 = ARGV[0]
file2 = ARGV[1]

# create new matcher with default threshold equals to 0
# and base (RGB) mode
cmp = Imatcher::Matcher.new
cmp.mode #=> Imatcher::Modes::RGB

# create matcher with specific threshold
cmp = Imatcher::Matcher.new threshold: 0.05
cmp.threshold #=> 0.05

# create zero-tolerance grayscale matcher 
cmp = Imatcher::Matcher.new mode: :grayscale, tolerance: 0
cmp.mode #=> Imatcher::Modes::Grayscale

res = cmp.compare(file1, file2)
res #=> Imatcher::Result

puts res.score

res.match? #=> true

res.score #=> 0.0



