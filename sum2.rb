require 'set'

def sum2(numbers)
	puts "Hello #{numbers}!"
	@numbers.each do |name|
		puts "Hello #{name}!"
	end

	if !@numbers.respond_to?("include?")
		raise "Parameter is not a set!"
	end

	a = Set.new [1,2,3]

	a.include? 2

	if
	elsif
	else
	end
end

if __FILE__ == $0
	begin
		File.open("readfile.rb", "r") do |infile|
		while (line = infile.gets)
			puts "#{counter}: #{line}"
			counter = counter + 1
		end
	end
	rescue => err
		puts "Exception: #{err}"
		err
	end
end
