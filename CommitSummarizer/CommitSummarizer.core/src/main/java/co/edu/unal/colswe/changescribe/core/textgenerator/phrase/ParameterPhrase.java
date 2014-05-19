package co.edu.unal.colswe.changescribe.core.textgenerator.phrase;

import co.edu.unal.colswe.changescribe.core.textgenerator.tokenizer.Tokenizer;

public class ParameterPhrase extends Phrase {
	private Parameter variable;
	StringBuilder phraseBuilder;

	public ParameterPhrase(final Parameter variable) {
		super();
		this.variable = variable;
	}

	public void generate() {
		this.phraseBuilder = new StringBuilder();
		final String splitType = Tokenizer.split(this.removeGeneric(this.variable.getTypeName()));
		final String splitVarName = Tokenizer.split(this.variable.getVariableName());
		if (this.variable.isPrimitive()) {
			this.phraseBuilder.append(splitVarName);
		} else if (splitType.toLowerCase().contains(splitVarName.toLowerCase())) {
			this.phraseBuilder.append(splitType);
		} else if (splitVarName.toLowerCase().contains(splitType.toLowerCase())) {
			this.phraseBuilder.append(splitVarName);
		} else {
			final String[] typeTokens = splitType.split(" ");
			final String[] varTokens = splitVarName.split(" ");
			String[] array;
			for (int length = (array = varTokens).length, j = 0; j < length; ++j) {
				final String varToken = array[j];
				for (int i = 0; i < typeTokens.length; ++i) {
					final String typeToken = typeTokens[i];
					if (typeToken.length() != 1) {
						if (varToken.toLowerCase().startsWith(typeToken.toLowerCase())) {
							this.phraseBuilder.append(String.valueOf(varToken) + " ");
							this.appendFromIndex(this.phraseBuilder,typeTokens, i + 1);
							return;
						}
						if (typeToken.toLowerCase().startsWith(varToken.toLowerCase())) {
							this.appendFromIndex(this.phraseBuilder, typeTokens, i);
							return;
						}
					}
				}
				this.phraseBuilder.append(String.valueOf(varToken) + " ");
			}
			String[] array2;
			for (int length2 = (array2 = typeTokens).length, k = 0; k < length2; ++k) {
				final String typeToken2 = array2[k];
				if (typeToken2.length() > 1) {
					this.phraseBuilder.append(String.valueOf(typeToken2) + " ");
				}
			}
		}
	}

	private void appendFromIndex(final StringBuilder phrase,
			final String[] tokens, final int i) {
		for (int j = i; j < tokens.length; ++j) {
			phrase.append(String.valueOf(tokens[j]) + " ");
		}
	}

	private String removeGeneric(final String typeName) {
		final int symbolIndex = typeName.indexOf("<");
		if (symbolIndex != -1) {
			return typeName.substring(0, symbolIndex);
		}
		return typeName;
	}

	public String toString() {
		return this.phraseBuilder.toString().trim();
	}
}
