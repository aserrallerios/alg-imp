require 'set'

def addTarget(targets, x, y)
	targets.add(x + y)
	#puts "#{x},#{y}"
end

def sum2(numbers, min, max)
	targets = Set.new

	$i = 0
	$j = numbers.length - 1

	while $i < $j do
		number_first = numbers[$i]
		number_last = numbers[$j]
		sum = number_first + number_last
		if sum < min
			$i = $i + 1
		elsif sum > max
			$j = $j - 1
		else
			$j.downto($i + 1) do 			
				|k| 
				number_last = numbers[k]
				sum = number_first + number_last
				if sum < min 
					break
				elsif number_first == number_last
					break
				end
				addTarget(targets, number_first, number_last)
			end
			$i = $i + 1
		end
	end

	return targets.size
end

if __FILE__ == $0
	if ARGV.length < 3
		abort "Usage: ruby sum2.rb <path_to_file> <min_num> <max_num>" 
	end
	begin
		numbers = Array.new
		File.open(ARGV[0], "r").each_line do |line|			
			numbers << line.to_i
		end
		puts "Distinct targets such that t = x + y (with x!=y) and t∈[#{ARGV[1]},#{ARGV[2]}]: #{sum2(numbers.sort, ARGV[1].to_i, ARGV[2].to_i)}" 
	rescue => err
		puts "Exception: #{err}"
		err
	end
end
