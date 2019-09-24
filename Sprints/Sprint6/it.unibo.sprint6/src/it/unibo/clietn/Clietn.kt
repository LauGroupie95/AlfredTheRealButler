/* Generated by AN DISI Unibo */ 
package it.unibo.clietn

import it.unibo.kactor.*
import alice.tuprolog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
	
class Clietn ( name: String, scope: CoroutineScope ) : ActorBasicFsm( name, scope){
 	
	override fun getInitialState() : String{
		return "s0"
	}
		
	override fun getBody() : (ActorBasicFsm.() -> Unit){
		return { //this:ActionBasciFsm
				state("s0") { //this:State
					action { //it:State
						println("init client")
					}
					 transition( edgeName="goto",targetState="ask", cond=doswitch() )
				}	 
				state("ask") { //this:State
					action { //it:State
					}
				}	 
			}
		}
}
