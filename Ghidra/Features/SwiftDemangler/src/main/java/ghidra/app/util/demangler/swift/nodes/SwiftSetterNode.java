/* ###
 * IP: GHIDRA
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ghidra.app.util.demangler.swift.nodes;

import ghidra.app.util.bin.format.swift.SwiftTypeMetadata;
import ghidra.app.util.demangler.*;
import ghidra.app.util.demangler.swift.SwiftDemangledNodeKind;
import ghidra.app.util.demangler.swift.SwiftDemangler;
import ghidra.app.util.demangler.swift.datatypes.SwiftDataTypeUtils;
import ghidra.app.util.demangler.swift.datatypes.SwiftFunction;
import ghidra.program.model.lang.CompilerSpec;

/**
 * A {@link SwiftDemangledNodeKind#Setter} {@link SwiftNode}
 */
public class SwiftSetterNode extends SwiftNode {

	@Override
	public Demangled demangle(SwiftDemangler demangler, SwiftTypeMetadata typeMetadata)
			throws DemangledException {
		Demangled demangled = null;
		String name = "set_";
		String callingConvention = CompilerSpec.CALLING_CONVENTION_thiscall;
		for (SwiftNode child : getChildren()) {
			switch (child.getKind()) {
				case Subscript:
					demangled = child.demangle(demangler, typeMetadata);
					break;
				case Variable:
					demangled = child.demangle(demangler, typeMetadata);
					break;
				default:
					skip(child);
					break;
			}
		}
		if (demangled instanceof DemangledFunction function) {
			function.setName(name + function.getName());
			function.setCallingConvention(callingConvention);
			return function;
		}
		if (demangled instanceof DemangledVariable variable) {
			SwiftFunction function =
				new SwiftFunction(properties.mangled(), properties.originalDemangled(),
					name + variable.getName(), variable.getNamespace(), callingConvention);
			function.addParameters(SwiftDataTypeUtils.extractParameters(variable));
			return function;
		}
		return getUnknown();
	}
}
