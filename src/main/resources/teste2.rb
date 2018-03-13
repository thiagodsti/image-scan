require "dhash-vips"

hash1 = DHashVips::DHash.calculate "gondola-cut.jpeg"
hash2 = DHashVips::DHash.calculate "coca.jpeg"

distance = DHashVips::DHash.hamming hash1, hash2
if distance < 10
	  puts "Images are very similar"
elsif distance < 20
	  puts "Images are slightly similar"
else
	  puts "Images are different"
end
