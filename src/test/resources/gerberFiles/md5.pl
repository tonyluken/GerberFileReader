#script expects one parameter (original Gerber X2 file) 
use Digest::MD5; 
local $_ = shift; 
local *IN; 
my $content; 
 
local $/; 
open(IN, "<$_") or die "Cannot open $_ due to $!"; 
$content = <IN>;        #read file to the variable 
close IN;  
$content =~ s/\r|\n//g; #remove all CRLF (end of line) characters 
$content =~ s/%TF\.MD5,[0-9A-Fa-f]*\*%[\s\S]*//;
$content =~ s/M02\*[\s\S]*//;   #remove M02 from the end of file 
#calculate MD5 
$md5 = Digest::MD5->new; #init MD5 
$md5->add($content);     #send content of the file to MD5 engine 
 
print "Add 2 following lines to the Gerber file, please.\n"; 
print "%TF.MD5,"; 
print $md5->hexdigest;   #print correct MD5 hash  
print "*%\nM02*\n\n";
