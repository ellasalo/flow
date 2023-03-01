import { ComponentTheme } from './model';
import { ComponentMetadata } from './metadata/model';

export function detectTheme(metadata: ComponentMetadata): ComponentTheme {
  const componentTheme = new ComponentTheme(metadata);
  const element = document.createElement(metadata.tagName);
  element.style.visibility = 'hidden';
  document.body.append(element);

  try {
    // Host
    const hostStyles = getComputedStyle(element);

    metadata.properties.forEach((property) => {
      const propertyValue = getPropertyValue(hostStyles, property.propertyName);
      componentTheme.updatePropertyValue(null, property.propertyName, propertyValue);
    });

    // Parts
    metadata.parts.forEach((part) => {
      const partElement = element.shadowRoot?.querySelector(`[part~="${part.partName}"]`);
      if (!partElement) {
        return;
      }
      const partStyles = getComputedStyle(partElement);

      part.properties.forEach((property) => {
        const propertyValue = getPropertyValue(partStyles, property.propertyName);
        componentTheme.updatePropertyValue(part.partName, property.propertyName, propertyValue);
      });
    });
  } finally {
    element.remove();
  }

  return componentTheme;
}

function getPropertyValue(styles: CSSStyleDeclaration, propertyName: string) {
  return propertyName.indexOf('--') === 0 ? styles.getPropertyValue(propertyName) : styles[propertyName as any];
}
