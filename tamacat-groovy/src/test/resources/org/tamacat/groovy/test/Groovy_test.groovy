package org.tamacat.groovy.test;

class Groovy_test {
	
	def static localMethod() {
		def v = new Date()
		return { println "closure:$v" }
	}
	
	static class Cat {
		def name
		def age
	}
	
	static main(args) {
		//["1","2","3"]every { value -> println value }
		def clos = localMethod()
		//Thread.sleep(1000)
		
        def d = new Date()
		println "test $d"
		
		// é¿çs
		clos()
		
		def cat = new Cat()
		cat['name'] = 'tama'
		println cat['name']
	}
}
