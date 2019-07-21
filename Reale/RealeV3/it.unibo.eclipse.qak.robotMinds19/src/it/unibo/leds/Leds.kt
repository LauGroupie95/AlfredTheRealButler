/* Generated by AN DISI Unibo */ 
package it.unibo.leds

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Leds ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						surpluss.ledManagerSupport.create(  )
						delay(10) 
						surpluss.ledManagerSupport.frontLedBlink( 250  )
					}
				}	 
			}
		}
}
