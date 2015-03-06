require 'RubyDataStructures'

def balanceHeaps(lo, hi)
	if lo.heapsize > hi.heapsize + 1
		i = lo.extract_maximum!
		hi.insert! -i
	elsif hi.heapsize > lo.heapsize
		i = hi.extract_maximum!
		lo.insert! -i
	end
end

def median(lo, hi)
	lo.maximum
end

def medians(numbers)
	result = 0
	lo = RubyDataStructures::MaxHeap.build([])
	hi = RubyDataStructures::MaxHeap.build([])

	numbers.each do |number|
		if lo.empty? || lo.maximum > number 
			lo.insert! number
		else
		 	hi.insert! -number 
		end
		balanceHeaps(lo, hi)
		result = result + median(lo, hi)
	end
	return result
end

if __FILE__ == $0
	if ARGV.length < 1
		abort "Usage: ruby median.rb <path_to_file>" 
	end
	begin
		numbers = Array.new
		File.open(ARGV[0], "r").each_line do |line|			
			numbers << line.to_i
		end
		puts "The sum of medians is: #{medians(numbers)}" 
	rescue => err
		puts "Exception: #{err}"
		err
	end
end
