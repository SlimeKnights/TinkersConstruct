require 'java'
require 'xinstick'
#java_package 'test'
#java_import cpw.mods.fml.common.Mod
#java_import cpw.mods.fml.common.Mod.Init

java_annotation 'Mod(modid = "JTest", name = "JTest", version = "Test")'
class JTest
  java_annotation 'Init'
  def init(event)
    puts 'Init, heyo!'
  end
end

#jest = JTest.new
#jest.init

#stick = XinStick.new(10000)

#cls = JTest.become_java!
#cls.declared_methods.each do |method|
  #puts method.simple_name
#end

puts 'Heyooooooooo'
