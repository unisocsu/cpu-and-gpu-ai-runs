class Calculator {
  constructor(previousOperandElement, currentOperandElement) {
    this.previousOperandElement = previousOperandElement;
    this.currentOperandElement = currentOperandElement;
    this.clear();
  }

  clear() {
    this.currentOperand = '0';
    this.previousOperand = '';
    this.operation = undefined;
    this.shouldResetScreen = false;
  }

  delete() {
    if (this.shouldResetScreen) return;
    if (this.currentOperand === '0') return;
    if (this.currentOperand.length === 1) {
      this.currentOperand = '0';
    } else {
      this.currentOperand = this.currentOperand.slice(0, -1);
    }
  }

  appendNumber(number) {
    if (this.shouldResetScreen) {
      this.currentOperand = '';
      this.shouldResetScreen = false;
    }
    if (number === '.' && this.currentOperand.includes('.')) return;
    if (this.currentOperand === '0' && number !== '.') {
      this.currentOperand = number;
    } else {
      this.currentOperand += number;
    }
  }

  chooseOperation(operation) {
    if (this.currentOperand === '') return;
    if (this.previousOperand !== '') {
      this.compute();
    }
    this.operation = operation;
    this.previousOperand = this.currentOperand;
    this.currentOperand = '';
  }

  compute() {
    let computation;
    const prev = parseFloat(this.previousOperand);
    const current = parseFloat(this.currentOperand);
    if (isNaN(prev) || isNaN(current)) return;

    switch (this.operation) {
      case '+':
        computation = prev + current;
        break;
      case '-':
        computation = prev - current;
        break;
      case '×':
        computation = prev * current;
        break;
      case '÷':
        if (current === 0) {
          alert('לא ניתן לחלק באפס!');
          this.clear();
          return;
        }
        computation = prev / current;
        break;
      default:
        return;
    }

    this.currentOperand = this.formatNumber(computation);
    this.operation = undefined;
    this.previousOperand = '';
    this.shouldResetScreen = true;
  }

  formatNumber(number) {
    if (Number.isNaN(number)) return '0';
    const stringNumber = number.toString();
    if (stringNumber.includes('.')) {
      const integerDigits = parseFloat(stringNumber.split('.')[0]);
      const decimalDigits = stringNumber.split('.')[1];
      return parseFloat(integerDigits.toLocaleString('en-US', { maximumFractionDigits: 0 })) + '.' + decimalDigits;
    }
    return number.toLocaleString('en-US', { maximumFractionDigits: 10 });
  }

  percentage() {
    const current = parseFloat(this.currentOperand);
    if (isNaN(current)) return;
    this.currentOperand = this.formatNumber(current / 100);
  }

  squareRoot() {
    const current = parseFloat(this.currentOperand);
    if (isNaN(current)) return;
    if (current < 0) {
      alert('לא ניתן לחשב שורש של מספר שלילי!');
      return;
    }
    this.currentOperand = this.formatNumber(Math.sqrt(current));
    this.shouldResetScreen = true;
  }

  square() {
    const current = parseFloat(this.currentOperand);
    if (isNaN(current)) return;
    this.currentOperand = this.formatNumber(Math.pow(current, 2));
    this.shouldResetScreen = true;
  }

  inverse() {
    const current = parseFloat(this.currentOperand);
    if (isNaN(current)) return;
    if (current === 0) {
      alert('לא ניתן לחלק באפס!');
      return;
    }
    this.currentOperand = this.formatNumber(1 / current);
    this.shouldResetScreen = true;
  }

  negate() {
    const current = parseFloat(this.currentOperand);
    if (isNaN(current)) return;
    this.currentOperand = this.formatNumber(current * -1);
  }

  getDisplayNumber(number) {
    const stringNumber = number.toString();
    const integerDigits = parseFloat(stringNumber.split('.')[0]);
    const decimalDigits = stringNumber.split('.')[1];
    let integerDisplay;
    if (isNaN(integerDigits)) {
      integerDisplay = '0';
    } else {
      integerDisplay = integerDigits.toLocaleString('en-US', { maximumFractionDigits: 0 });
    }
    if (decimalDigits != null) {
      return `${integerDisplay}.${decimalDigits}`;
    } else {
      return integerDisplay;
    }
  }

  updateDisplay() {
    this.currentOperandElement.innerText = this.getDisplayNumber(this.currentOperand);
    if (this.operation != null) {
      this.previousOperandElement.innerText = 
        `${this.getDisplayNumber(this.previousOperand)} ${this.operation}`;
    } else {
      this.previousOperandElement.innerText = '';
    }
  }
}

// אתחול המחשבון
const previousOperandElement = document.getElementById('previousOperand');
const currentOperandElement = document.getElementById('currentOperand');
const calculator = new Calculator(previousOperandElement, currentOperandElement);

// הוספת מאזיני אירועים
document.querySelectorAll('[data-number]').forEach(button => {
  button.addEventListener('click', () => {
    calculator.appendNumber(button.innerText);
    calculator.updateDisplay();
  });
});

document.querySelectorAll('[data-operation]').forEach(button => {
  button.addEventListener('click', () => {
    calculator.chooseOperation(button.innerText);
    calculator.updateDisplay();
  });
});

document.querySelector('[data-action="equals"]').addEventListener('click', () => {
  calculator.compute();
  calculator.updateDisplay();
});

document.querySelector('[data-action="clear"]').addEventListener('click', () => {
  calculator.clear();
  calculator.updateDisplay();
});

document.querySelector('[data-action="delete"]').addEventListener('click', () => {
  calculator.delete();
  calculator.updateDisplay();
});

document.querySelector('[data-action="percent"]').addEventListener('click', () => {
  calculator.percentage();
  calculator.updateDisplay();
});

document.querySelector('[data-action="sqrt"]').addEventListener('click', () => {
  calculator.squareRoot();
  calculator.updateDisplay();
});

document.querySelector('[data-action="square"]').addEventListener('click', () => {
  calculator.square();
  calculator.updateDisplay();
});

document.querySelector('[data-action="inverse"]').addEventListener('click', () => {
  calculator.inverse();
  calculator.updateDisplay();
});

document.querySelector('[data-action="negate"]').addEventListener('click', () => {
  calculator.negate();
  calculator.updateDisplay();
});

// תמיכה במקלדת
document.addEventListener('keydown', (e) => {
  if (e.key >= '0' && e.key <= '9') calculator.appendNumber(e.key);
  if (e.key === '.') calculator.appendNumber('.');
  if (e.key === '+') calculator.chooseOperation('+');
  if (e.key === '-') calculator.chooseOperation('-');
  if (e.key === '*') calculator.chooseOperation('×');
  if (e.key === '/') { e.preventDefault(); calculator.chooseOperation('÷'); }
  if (e.key === 'Enter' || e.key === '=') calculator.compute();
  if (e.key === 'Backspace') calculator.delete();
  if (e.key === 'Escape') calculator.clear();
  calculator.updateDisplay();
});
